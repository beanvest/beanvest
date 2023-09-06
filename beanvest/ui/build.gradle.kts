plugins {
}

group = "beanvest.ui"
val npm = "/nix/store/1gnvy2dhh311c900hzyw6ppjdhnir2s5-nodejs-20.5.0/bin/npm";

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