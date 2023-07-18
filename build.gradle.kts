import org.gradle.api.JavaVersion.VERSION_17

plugins {
    id("com.adarshr.test-logger") version "3.2.0"
    id("java")

}
java {
    sourceCompatibility = VERSION_17
    targetCompatibility = VERSION_17
}
allprojects {
    apply(plugin = "java")
    apply(plugin = "idea")
}
