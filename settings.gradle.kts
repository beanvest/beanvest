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
            version("logback", "1.4.8")
            version("slf4j", "1.7.36")

            //bundles
            bundle("logging", listOf("slf4j", "logback"))
            library("slf4j", "org.slf4j", "slf4j-api").versionRef("slf4j")
            library("logback", "ch.qos.logback", "logback-classic").versionRef("logback")

            bundle("testing", listOf("junitApi", "junitEngine", "assertj"))
            library("junitApi", "org.junit.jupiter", "junit-jupiter-api").versionRef("junit")
            library("junitEngine", "org.junit.jupiter", "junit-jupiter-engine").versionRef("junit")

            library("assertj", "org.assertj", "assertj-core").versionRef("assertj")
            library("gson", "com.google.code.gson", "gson").version("2.10.1")
            library("jsonAssert", "org.skyscreamer", "jsonassert").version("1.5.1")
            library("openCsv", "com.opencsv", "opencsv").version("5.7.1")
            library("picocli", "info.picocli", "picocli").version("4.7.4")
            library("xirr", "org.decampo", "xirr").version("1.2")

            // https://mvnrepository.com/artifact/cz.habarta.typescript-generator/typescript-generator-maven-plugin
            library("typescriptGenerator", "cz.habarta.typescript-generator", "typescript-generator-maven-plugin").version("3.2.1263")
        }
    }
}
