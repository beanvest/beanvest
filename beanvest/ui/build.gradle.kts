plugins {
}

group = "beanvest.ui"
val npm = "/nix/store/1gnvy2dhh311c900hzyw6ppjdhnir2s5-nodejs-20.5.0/bin/npm";

tasks.register("jsInstall") {
    doLast {
        exec {
            commandLine("$npm", "install")
        }
    }
}

tasks.register("jsTest") {
    dependsOn("jsInstall")
    doLast {
        exec {
            commandLine("$npm", "run", "test:unit", "--", "--run")
        }
    }
}