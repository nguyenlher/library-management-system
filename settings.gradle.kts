rootProject.name = "demo"

pluginManagement {
	plugins {
		id("org.springframework.boot") version "3.5.8"
	}

	repositories {
		gradlePluginPortal()
		mavenCentral()
	}
}

include("api-gateway")
include("auth-service")
include("book-service")
include("borrow-service")
include("notification-service")
include("payment-service")
include("user-service")
