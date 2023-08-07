import org.gradle.api.JavaVersion.VERSION_17

plugins {
    id("java")
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "idea")
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