plugins {
    id("java")
    id("org.graalvm.buildtools.native") version "0.9.23"
}

group = "beanvest.beanvest"

dependencies {
    implementation(libs.bundles.logging)
    implementation(project(":beanvest:lib:clitable"))
    implementation(project(":beanvest:lib:jsonassert"))
    implementation(project(":beanvest:lib:util"))
    implementation(libs.picocli)
    implementation(libs.gson)
    implementation(libs.openCsv)
    implementation(libs.xirr)

    testImplementation(project(":beanvest:lib:testing"))
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
}

graalvmNative {
    binaries {
        named("main") {
            mainClass.set("beanvest.BeanvestMain")
            quickBuild.set(true)
            buildArgs.add("-H:Features=beanvest.module.returns.GraalvmReflectionRegistrar")
        }
    }
}