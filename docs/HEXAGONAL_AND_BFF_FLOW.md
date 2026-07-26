# Hexagonal Architecture & Backend-for-Frontend (BFF) Workflow

## 1. High-Level System Topology (BFF + Microservices)

In a modern client ecosystem (Web App, Mobile App, Social Media Automation Bots), a **Backend-For-Frontend (BFF)** layer sits between clients and core domain microservices like `ahun-duty-service`.

The **BFF** handles:
- Client-specific DTO aggregation (*e.g., combining Duty details + Theme preview + Card URL in a single response*).
- Authentication / Authorization headers validation.
- Response shaping and image caching headers.

```mermaid
graph TD
    subgraph Clients ["Client Layer"]
        WEB["🌐 Admin / Web Dashboard"]
        MOB["📱 Mobile App"]
        BOT["🤖 Social Media Automation Bot"]
    end

    subgraph BFF_Layer ["BFF Architecture Layer"]
        BFF["🛡️ Ahun Web & Mobile BFF (API Gateway / BFF Service)"]
    end

    subgraph Core_Services ["Domain Microservices Layer"]
        DUTY_SVC["⚡ Ahun Duty Service (Hexagonal Core)"]
        AUTH_SVC["🔐 Auth & Member Service (Optional)"]
    end

    WEB -->|HTTP / JSON| BFF
    MOB -->|HTTP / JSON| BFF
    BOT -->|HTTP / PNG Render| BFF

    BFF -->|gRPC / REST| DUTY_SVC
    BFF -->|REST| AUTH_SVC
```

---

## 2. Hexagonal Architecture (Ports & Adapters) Inside `ahun-duty-service`

The `ahun-duty-service` strictly segregates domain business logic from framework and infrastructure code using Hexagonal Architecture.

```mermaid
graph TB
    subgraph External ["External World"]
        BFF_CALL["BFF / Client"]
    end

    subgraph Hexagon ["Ahun Duty Service (Hexagon Core)"]
        
        subgraph Primary_Adapters ["Primary / Inbound Adapters"]
            REST_CTRL["DutyResource / CardResource<br/>(REST Controllers)"]
        end

        subgraph Inbound_Ports ["Inbound Ports (Use Cases Interface)"]
            CARD_PORT_IN["CardUsecasePort"]
            DUTY_PORT_IN["DutyUsecasePort"]
        end

        subgraph Domain_Core ["Application & Domain Core"]
            CARD_UC["CardUsecase (Application Service)"]
            DUTY_UC["DutyUsecase (Application Service)"]
            DOMAIN_ENT["Domain Entities:<br/>Duty, Theme, DutyEvent, Template"]
        end

        subgraph Outbound_Ports ["Outbound Ports (Interfaces)"]
            DUTY_REPO_PORT["DutyRepositoryPort"]
            RENDER_PORT["CardRenderPort"]
            STORAGE_PORT["FileStoragePort"]
        end

        subgraph Secondary_Adapters ["Secondary / Outbound Adapters"]
            JPA_ADAPTER["PostgresqlJpaAdapter<br/>(Spring Data JPA)"]
            RENDER_ADAPTER["ThymeleafCardRendererAdapter<br/>(Flying Saucer + PDFBox)"]
            STORAGE_ADAPTER["LocalStorageAdapter / S3Adapter"]
        end
    end

    subgraph Infra ["Infrastructure / Storage"]
        DB[(PostgreSQL Database)]
        DISK[("Image Storage / AWS S3")]
    end

    BFF_CALL -->|HTTP Request| REST_CTRL
    REST_CTRL -->|Calls| CARD_PORT_IN
    CARD_PORT_IN -.->|Implemented by| CARD_UC
    
    CARD_UC --> DOMAIN_ENT
    CARD_UC -->|Uses| DUTY_REPO_PORT
    CARD_UC -->|Uses| RENDER_PORT
    CARD_UC -->|Uses| STORAGE_PORT

    DUTY_REPO_PORT -.->|Implemented by| JPA_ADAPTER
    RENDER_PORT -.->|Implemented by| RENDER_ADAPTER
    STORAGE_PORT -.->|Implemented by| STORAGE_ADAPTER

    JPA_ADAPTER -->|SQL Queries| DB
    RENDER_ADAPTER -->|HTML -> PDF -> PNG| RENDER_ADAPTER
    STORAGE_ADAPTER -->|Read/Write Bytes| DISK
```

---

## 3. End-to-End Sequence Flow: Requesting a Card PNG

Below is the complete trace when a mobile/web user triggers a card generation request via the BFF down to the Flying Saucer + PDFBox rendering pipeline inside `ahun-duty-service`.

```mermaid
sequenceDiagram
    autonumber
    actor User as Client (Web / Mobile)
    participant BFF as Backend for Frontend (BFF)
    participant Controller as CardResource (Inbound Adapter)
    participant UseCase as CardUsecase (Application Layer)
    participant Domain as Duty & Theme (Domain Core)
    participant RepoPort as DutyRepositoryPort (Outbound Port)
    participant DBAdapter as PostgresDutyRepository (Outbound Adapter)
    participant RenderPort as CardRenderPort (Outbound Port)
    participant RenderAdapter as ThymeleafCardRendererAdapter (Outbound Adapter)
    participant PDFBox as PDFBox & Flying Saucer Engine

    User->>BFF: GET /api/v1/cards/{dutyId}/download
    BFF->>BFF: Authenticate user token & validate params
    BFF->>Controller: GET /cards/{dutyId}/render

    Note over Controller, UseCase: Entering Hexagon via Inbound Port
    Controller->>UseCase: renderCardPng(dutyId)
    
    UseCase->>RepoPort: findById(dutyId)
    RepoPort->>DBAdapter: findById(dutyId)
    DBAdapter-->>RepoPort: DutyEntity -> map to Duty Domain model
    RepoPort-->>UseCase: Duty (Domain Model)

    UseCase->>Domain: Resolve Theme, Events, and Date formatting
    Domain-->>UseCase: Formatted CardData

    Note over UseCase, RenderAdapter: Exiting Hexagon via Outbound Port
    UseCase->>RenderPort: renderPng("2_fields_template", variables)
    RenderPort->>RenderAdapter: renderPng(templateName, variables)

    RenderAdapter->>RenderAdapter: Thymeleaf Engine compiles HTML
    RenderAdapter->>PDFBox: Flying Saucer ITextRenderer generates PDF
    RenderAdapter->>PDFBox: Apache PDFBox renders PDF to PNG BufferedImage (300 DPI)
    RenderAdapter-->>RenderPort: PNG ByteArray
    RenderPort-->>UseCase: PNG ByteArray

    UseCase-->>Controller: PNG ByteArray
    Controller-->>BFF: 200 OK (image/png)
    BFF-->>User: Binary Image Stream / Attachment
```

---

## 4. Key Architectural Pillars

### 1. BFF Layer Responsibility
- **Decoupling**: Prevents frontends from depending on internal microservice entity schemas.
- **Aggregation**: A single BFF call can query `/duties/{id}`, fetch the `/cards/{id}/preview` HTML snippet, and package it into a rich JSON for the UI.
- **Optimized Delivery**: Handles caching headers (`Cache-Control`, `ETag`) for rendered cards.

### 2. Hexagonal Architecture Benefits in `ahun-duty-service`
- **Port Swappability**: Flying Saucer + PDFBox is encapsulated behind `CardRenderPort`. If requirements change to Playwright, Puppeteer, or AWS Lambda rendering, **zero business logic in `CardUsecase` changes**.
- **Database Independence**: Domain model `Duty` is isolated from JPA `DutyEntity` via `DutyRepositoryPort`.
- **Testability**: `CardUsecase` can be thoroughly unit tested using simple mocks for `CardRenderPort` and `DutyRepositoryPort` without starting Spring contexts or databases.
