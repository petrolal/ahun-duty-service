plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.kotlin.jpa)
}

group = "com.petrolal.ahun"
version = "0.0.1-SNAPSHOT"
description = "ahun-duty-service"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.flyway)
    implementation(libs.spring.boot.starter.thymeleaf)
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.sprint.boot.hateoas)
    implementation(libs.flyway.database.postgresql)
    implementation(libs.xhtmlrenderer.flyingSaucerCore)
    implementation(libs.kotlin.reflect)
    implementation(libs.spring.doc.openapi)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.microsoft.playwright)
    developmentOnly(libs.spring.boot.devtools)
    developmentOnly(libs.spring.boot.docker.compose)
    runtimeOnly(libs.postgresql)
    testImplementation(libs.sprint.boot.starter.test)
    testImplementation(libs.kotlin.mockito)
    testImplementation(libs.kotlin.test.junit5)
    testRuntimeOnly(libs.junit.platform.launcher)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
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
