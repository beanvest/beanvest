plugins {
    id("java")
}

group = "beanvest.acceptance"

dependencies {
    implementation(project(":beanvest:beanvest"))
    implementation(project(":beanvest:lib:apprunner"))
    implementation(project(":beanvest:scripts:usagegen"))

    testImplementation(libs.bundles.testing)
}

tasks {
    register<JavaExec>("bench") {
        dependsOn(":beanvest:beanvest:nativeCompile")
        classpath = sourceSets.main.get().runtimeClasspath
        environment("NATIVE_BIN_PATH", "${project.rootDir}/beanvest/beanvest/build/native/nativeCompile/beanvest")
        mainClass = "beanvest.benchmark.BenchmarkMain"
    }
}