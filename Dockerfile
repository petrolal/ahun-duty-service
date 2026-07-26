# Stage 1: Build
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

ARG GITHUB_ACTOR
ARG GITHUB_TOKEN
ENV GITHUB_ACTOR=${GITHUB_ACTOR}
ENV GITHUB_TOKEN=${GITHUB_TOKEN}

COPY gradle ./gradle
COPY gradlew build.gradle.kts settings.gradle.kts ./
COPY src ./src

RUN ./gradlew bootJar --no-daemon -x test

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

RUN addgroup -S ahun && adduser -S ahun -G ahun
USER ahun

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseSerialGC", "-XX:TieredStopAtLevel=1", "-Xss256k", "-Xms32m", "-Xmx192m", "-jar", "app.jar"]
