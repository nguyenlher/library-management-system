val lombokVersion: String by project

plugins {
    id("org.springframework.boot") version "3.5.8"
    id("io.spring.dependency-management") version "1.1.6"
    java
}

group = "com.library"
version = "0.0.1-SNAPSHOT"
java {
    sourceCompatibility = JavaVersion.VERSION_21
    toolchain {
        languageVersion.set(org.gradle.jvm.toolchain.JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.0")
    }
}

dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
	