plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.kotlin.jpa)
    `maven-publish`
}

group = "com.petrolal.ahun"
version = "0.0.1"
description = "ahun-duty-service"

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/petrolal/ahun-duty-service")
            credentials {
                username = System.getenv("GITHUB_ACTOR") ?: "petrolal"
                password = System.getenv("GITHUB_TOKEN") ?: System.getenv("GH_PAT")
            }
        }
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/petrolal/spring-commons-web")
        credentials {
            username = System.getenv("GITHUB_ACTOR") ?: "petrolal"
            password = System.getenv("GITHUB_TOKEN") ?: System.getenv("GH_PAT")
        }
    }
}

dependencies {
    implementation(libs.petrolal.commons.web)
    implementation(libs.spring.boot.starter.thymeleaf)
    implementation(libs.xhtmlrenderer.flyingSaucerCore)
    implementation(libs.xhtmlrenderer.flyingSaucerPdfOpenpdf)
    implementation(libs.apache.pdfbox)

    testImplementation(libs.kotlin.mockito)
    testImplementation(libs.kotlin.test.junit5)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}