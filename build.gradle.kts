import org.gradle.api.JavaVersion.VERSION_17

plugins {
    id("com.adarshr.test-logger") version "3.2.0"
    id("java")
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "idea")
    apply(plugin = "com.adarshr.test-logger")
    java {
        sourceCompatibility = VERSION_17
        targetCompatibility = VERSION_17
        version = "1.0-SNAPSHOT"
        repositories {
            mavenCentral()
            gradlePluginPortal()
        }
    }
}