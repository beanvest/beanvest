plugins {
    id("java")
    id("org.graalvm.buildtools.native") version "0.9.23"
}

group = "beanvest"
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
    implementation(libs.bundles.logging)
    implementation(project(":lib:clitable"))
    implementation(project(":lib:jsonassert"))
    implementation(project(":lib:util"))
    implementation(libs.picocli)
    implementation(libs.gson)
    implementation(libs.openCsv)
    implementation(libs.xirr)

    testImplementation(project(":lib:testing"))
    testImplementation(libs.bundles.testing)
    testImplementation(libs.openCsv)
}

tasks {
    test {
        useJUnitPlatform()
        testLogging {
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
    }

    val uberjar = register<Jar>("uberjar") {
        manifest.attributes["Main-Class"] = "beanvest.BeanvestMain"
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        from(configurations.runtimeClasspath.get()
                .map { if (it.isDirectory) it else zipTree(it) })
        from(sourceSets.main.get().output)

        @Suppress("UNUSED_VARIABLE")
        val jarPath: String by extra(archiveFile.get().toString())
    }

    register<Test>("testfast") {
        useJUnitPlatform()
        maxParallelForks = 4
        testLogging {
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
    }
    register<Test>("testfull") {
        dependsOn("testfast")
        dependsOn("uberjar")

        val jarPath: String by uberjar.get().extra
        useJUnitPlatform()
        maxParallelForks = Runtime.getRuntime().availableProcessors()
        environment("ACCEPTANCE_JAR_PATH", jarPath)

        filter {
            includeTestsMatching("*AcceptanceTest")
        }

        testLogging {
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
    }
}

graalvmNative {

    binaries {
        named("main") {
            mainClass.set("beanvest.BeanvestMain")
        }
    }
}