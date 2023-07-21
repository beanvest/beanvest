rootProject.name = "bb"
include(
        "beanvest:beanvest",
        "beanvest:lib:clitable",
        "beanvest:lib:jsonassert",
        "beanvest:lib:testing",
        "beanvest:lib:util",
        "beanvest:scripts",
)

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            //last mass-upgrade: 2022-06-18

            version("assertj", "3.23.1")
            version("jackson", "2.14.2")
            version("junit", "5.8.2")
            version("junit4", "4.13.2")
            version("logback", "1.2.6")
            version("slf4j", "1.7.30")

            //bundles
            bundle("logging", listOf("slf4j", "logback"))
            library("slf4j", "org.slf4j", "slf4j-api").versionRef("slf4j")
            library("logback", "ch.qos.logback", "logback-classic").versionRef("logback")

            bundle("testing", listOf("junitApi", "junitEngine", "assertj"))
            library("junitApi", "org.junit.jupiter", "junit-jupiter-api").versionRef("junit")
            library("junitEngine", "org.junit.jupiter", "junit-jupiter-engine").versionRef("junit")

            bundle("testingv4", listOf("junit4", "junitEngine", "assertj"))
            library("junit4", "junit", "junit").versionRef("junit4")

            bundle("jackson", listOf("jacksonDatabind", "jacksonJdk8"))

            bundle("jacksonYaml", listOf("jacksonDatabind", "jacksonDataformatYaml"))
            library(
                    "jacksonDataformatYaml",
                    "com.fasterxml.jackson.dataformat",
                    "jackson-dataformat-yaml"
            ).versionRef("jackson")

            library("assertj", "org.assertj", "assertj-core").versionRef("assertj")
            library("apacheCommonsImaging", "org.apache.commons", "commons-imaging").version("1.0-alpha2")
            library("gson", "com.google.code.gson", "gson").version("2.10.1")
            library("ini4j", "org.ini4j", "ini4j").version("0.5.4")
            library("jsonAssert", "org.skyscreamer", "jsonassert").version("1.5.0")
            library("jacksonDatabind", "com.fasterxml.jackson.core", "jackson-databind").versionRef("jackson")
            library("jacksonJdk8", "com.fasterxml.jackson.datatype", "jackson-datatype-jdk8").versionRef("jackson")
            library("openCsv", "com.opencsv", "opencsv").version("5.6")
            library("picocli", "info.picocli", "picocli").version("4.6.3")
            library("rome", "com.rometools", "rome").version("1.18.0")
            library("xirr", "org.decampo", "xirr").version("1.2")
        }
    }
}
