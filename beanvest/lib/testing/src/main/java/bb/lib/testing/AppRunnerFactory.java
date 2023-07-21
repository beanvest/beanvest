package bb.lib.testing;

import bb.lib.testing.apprunner.DirectRunner;
import bb.lib.testing.apprunner.JarRunner;
import bb.lib.util.apprunner.BaseMain;

import java.util.Optional;

public class AppRunnerFactory {

    public static AppRunner createRunner(Class<? extends BaseMain> mainClass, String subcommand) {
        return actuallyCreateRunner(mainClass, Optional.of(subcommand));
    }

    public static AppRunner createRunner(Class<? extends BaseMain> mainClass) {
        return actuallyCreateRunner(mainClass, Optional.empty());
    }

    private static AppRunner actuallyCreateRunner(Class<? extends BaseMain> mainClass, Optional<String> subcommand) {
        var jar = System.getenv("ACCEPTANCE_JAR_PATH");
        return jar == null
                ? new DirectRunner(mainClass, subcommand)
                : new JarRunner(jar, subcommand);
    }
}
