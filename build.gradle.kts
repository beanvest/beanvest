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
tasks.register("generate") {
    dependsOn(":beanvest:scripts:usagegen:generateSampleJournal",
            ":beanvest:scripts:usagegen:generateUsageDoc",
            ":beanvest:scripts:usagegen:generateSampleJson",
            ":beanvest:scripts:tsgen:generateTypescriptTypes")
    group = "generation"
    description = "Regenerates everything"
}
tasks.register("nativeGenerate") {
    dependsOn(":beanvest:scripts:usagegen:generateSampleJournal",
            ":beanvest:scripts:usagegen:generateUsageDocNative",
            ":beanvest:scripts:usagegen:generateSampleJsonNative",
            ":beanvest:scripts:tsgen:generateTypescriptTypes")
    group = "generation"
    description = "Regenerates everything using native build"
}

tasks.register("all") {
    group = "all"
    description = "build, test and generate."
    dependsOn("build", "test", "generate")
}
tasks.register("nativeAll") {
    group = "all"
    description = "build, test and generate using native binaries."
    dependsOn("all",
            ":beanvest:acceptance:nativeTest",
            ":beanvest:beanvest:nativeTest",
            "nativeGenerate")
}
