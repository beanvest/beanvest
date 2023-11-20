plugins {
}

group = "beanvest.ui"
val npm = "/nix/store/sfwmhcpz0khf2dr98cdys8wq5wx7slcq-nodejs-20.5.1/bin/npm";

tasks.register("uiInstallDeps") {
    doLast {
        exec {
            commandLine("$npm", "install")
        }
    }
}

tasks.register("uiTest") {
    dependsOn("uiInstallDeps")
    doLast {
        exec {
            commandLine("$npm", "run", "test:unit", "--", "--run")
        }
    }
}

tasks.register("uiDev") {
    dependsOn("uiInstallDeps")
    doLast {
        exec {
            // does not clean up the forked process when interrupted
            // https://github.com/gradle/gradle/issues/7603
            commandLine("$npm", "run", "dev")
        }
    }
}
