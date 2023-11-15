import org.gradle.api.JavaVersion.VERSION_19
import org.gradle.api.JavaVersion.VERSION_21

plugins {
    id("java")
    id("application")
}


allprojects {
    apply(plugin = "java")
    apply(plugin = "idea")
    java {
        sourceCompatibility = VERSION_21
        targetCompatibility = VERSION_21
        version = "1.0-SNAPSHOT"
        repositories {
            mavenCentral()
            gradlePluginPortal()
        }
    }
}

tasks.register("generate") {
    dependsOn(":beanvest:scripts:usagegen:generateSampleJournal",
            ":beanvest:scripts:usagegen:generateUsageDoc",
            ":beanvest:scripts:usagegen:generateSampleReportJson",
            ":beanvest:scripts:usagegen:generateSampleOptions",
            ":beanvest:scripts:tsgen:generateTypescriptTypes")
    group = "generation"
    description = "Regenerates everything"
}
tasks.register("nativeGenerate") {
    dependsOn(":beanvest:scripts:usagegen:generateSampleJournal",
            ":beanvest:scripts:usagegen:generateUsageDocNative",
            ":beanvest:scripts:usagegen:generateSampleReportJsonNative",
            ":beanvest:scripts:tsgen:generateTypescriptTypes")
    group = "generation"
    description = "Regenerates everything using native build"
}

tasks.register("all") {
    group = "all"
    description = "build, test and generate."
    dependsOn("build",
            "test",
            ":beanvest:ui:uiTest",
            "generate")
}
tasks.register("nativeAll") {
    group = "all"
    description = "build, test and generate using native binaries."
    dependsOn("all",
            ":beanvest:acceptance:nativeTest",
            ":beanvest:beanvest:nativeTest",
            "nativeGenerate")
}
