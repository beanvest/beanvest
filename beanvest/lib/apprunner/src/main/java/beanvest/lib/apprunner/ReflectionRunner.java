package beanvest.lib.apprunner;

import beanvest.lib.apprunner.main.BaseMain;
import beanvest.lib.apprunner.main.CliWriters;
import beanvest.lib.apprunner.main.FakeSystemExitException;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReflectionRunner implements AppRunner {
    private final Class<?> mainClass;
    private final Optional<String> maybeSubcommand;

    public ReflectionRunner(Class<? extends BaseMain> mainClass, Optional<String> subcommand1) {
        this.mainClass = mainClass;
        this.maybeSubcommand = subcommand1;
    }

    private CliExecutionResult runApp(List<String> args, List<String> appArgs) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, IOException {
        setReflectionRunnerFlag();
        var initMethod = mainClass.getMethod("init");
        var mainMethod = mainClass.getMethod("main", String[].class);
        var getExitCodeMethod = mainClass.getMethod("getExitCode");

        var cliWriters = (CliWriters) initMethod.invoke(null);
        var printStream = new PrintStream(cliWriters.err(), false, StandardCharsets.UTF_8);
        ProducingLogAppender.setConsumer((event) -> printStream.println(event.getFormattedMessage()));

        final String[] argsArray = appArgs.toArray(new String[0]);
        try {
            mainMethod.invoke(null, (Object) argsArray);
        } catch (InvocationTargetException e) {
            if (!e.getTargetException().getClass().equals(FakeSystemExitException.class)) {
                throw e;
            }
        }
        cliWriters.out().flush();
        cliWriters.err().flush();
        ProducingLogAppender.clear();
        return new CliExecutionResult(args,
                cliWriters.out().toString(StandardCharsets.UTF_8),
                cliWriters.err().toString(StandardCharsets.UTF_8),
                (int) getExitCodeMethod.invoke(null));
    }

    private static void setReflectionRunnerFlag() {
        System.setProperty("beanvest.runningViaReflectionRunner", "true");
    }
    public static boolean isRunningWithReflectionRunner() {
        return "true".equals(System.getProperty("beanvest.runningViaReflectionRunner"));
    }

    @Override
    public CliExecutionResult run(List<String> args) {
        return run(List.of(), args);
    }

    @Override
    public CliExecutionResult run(List<String> vmParams, List<String> args) {
        try {
            var appArgs = new ArrayList<>(args);
            maybeSubcommand.ifPresent(subcommand -> appArgs.add(0, subcommand));
            vmParams.stream()
                    .filter(param -> param.startsWith("-D"))
                    .map(param -> param.substring(2))
                    .map(param -> {
                        var eqIndex = param.indexOf("=");
                        return new VmParam(param.substring(0, eqIndex), param.substring(eqIndex + 1));
                    })
                    .forEach(p -> System.setProperty(p.key, p.value));
            return runApp(args, appArgs);
        } catch (IOException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    record VmParam(String key, String value) {

    }

    @Override
    public CliExecutionResult runSuccessfully(List<String> args) {
        var cliExecutionResult = run(args);
        if (cliExecutionResult.exitCode() != 0) {
            throw new NonZeroExitCodeException(cliExecutionResult);
        }
        return cliExecutionResult;
    }

    @Override
    public CliExecutionResult runSuccessfully(List<String> vmParams, List<String> args) {
        throw new UnsupportedOperationException();
    }
}
