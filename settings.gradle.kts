rootProject.name = "beanvest"
include(
        "beanvest:acceptance",
        "beanvest:beanvest",
        "beanvest:ui",
        "beanvest:lib:apprunner",
        "beanvest:lib:clitable",
        "beanvest:lib:jsonassert",
        "beanvest:lib:testing",
        "beanvest:lib:util",
        "beanvest:scripts:usagegen",
        "beanvest:scripts:tsgen",
)

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            //last mass-upgrade: 2023-07-23

            version("assertj", "3.24.2")
            version("jackson", "2.15.2")
            version("junit", "5.9.3")
            version("logback", "1.4.11")
            version("slf4j", "1.7.36")

            version("gson", "2.10.1")
            version("jsonAssert", "1.5.1")
            version("openCsv", "5.8")
            version("picocli", "4.7.4")
            version("xirr", "1.2")

            //bundles
            bundle("logging", listOf("slf4j", "logback"))
            library("slf4j", "org.slf4j", "slf4j-api").versionRef("slf4j")
            library("logback", "ch.qos.logback", "logback-classic").versionRef("logback")

            bundle("testing", listOf("junitApi", "junitEngine", "assertj"))
            library("junitApi", "org.junit.jupiter", "junit-jupiter-api").versionRef("junit")
            library("junitEngine", "org.junit.jupiter", "junit-jupiter-engine").versionRef("junit")

            library("assertj", "org.assertj", "assertj-core").versionRef("assertj")
            library("gson", "com.google.code.gson", "gson").versionRef("gson")
            library("jsonAssert", "org.skyscreamer", "jsonassert").versionRef("jsonAssert")
            library("openCsv", "com.opencsv", "opencsv").versionRef("openCsv")
            library("picocli", "info.picocli", "picocli").versionRef("picocli")
            library("xirr", "org.decampo", "xirr").versionRef("xirr")

            // https://mvnrepository.com/artifact/cz.habarta.typescript-generator/typescript-generator-maven-plugin
            library("typescriptGenerator", "cz.habarta.typescript-generator", "typescript-generator-maven-plugin").version("3.2.1263")
        }
    }
}
