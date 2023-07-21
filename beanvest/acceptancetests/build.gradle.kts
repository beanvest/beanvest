plugins {
    id("java")
}

group = "beanvest.acceptancetests"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
    gradlePluginPortal()

}

dependencies {
    testImplementation(project(":beanvest:beanvest"))
    testImplementation(project(":beanvest:lib:jsonassert"))
    testImplementation(project(":beanvest:lib:util"))
    testImplementation(project(":beanvest:lib:testing"))
    testImplementation(libs.bundles.testing)
    testImplementation(libs.gson)
    testImplementation(libs.openCsv)
}

tasks {
    test {
        useJUnitPlatform()
        testLogging {
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
    }
    register<Test>("acceptanceTests") {
        dependsOn(":beanvest:beanvest:nativeCompile")

        maxParallelForks = Runtime.getRuntime().availableProcessors()
        environment("ACCEPTANCE_BIN_PATH", "beanvest/beanvest/build/native/nativeCompile/beanvest")
        useJUnitPlatform()
        testLogging {
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
    }
}