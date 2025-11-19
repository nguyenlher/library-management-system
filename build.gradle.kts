plugins {
}

group = "com.library"
version = "0.0.1-SNAPSHOT"
description = "Demo project for Spring Boot"

repositories {
	mavenCentral()
}

tasks.withType<Test> {
	useJUnitPlatform()
}
