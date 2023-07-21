package bb.lib.testing.apprunner;

import bb.lib.testing.AppRunner;
import bb.lib.testing.CliExecutionResult;
import bb.lib.testing.NonZeroExitCodeException;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;


public class JarRunner implements AppRunner {
    public static final String JAVA_HOME = System.getProperty("java.home");
    public static final String JAVA_BIN = JAVA_HOME + "/bin/java";
    private static final CountDownLatch buildLatch = new CountDownLatch(1);
    public static final Function<String, Boolean> DONT_BLOCK = line -> true;
    public static final Consumer<String> NOOP_CONSUMER = line -> {
    };
    private static boolean buildingStarted = false;
    private final String jarPath;
    private final Executor executor;
    private final Optional<String> maybeSubcommand;

    private CountDownLatch countDownLatch;

    public JarRunner(String jarPath) {
        this(jarPath, Optional.empty());
    }

    public JarRunner(String jarPath, Optional<String> subcommand1) {
        this(jarPath, Executors.newSingleThreadExecutor(), subcommand1);
    }

    public JarRunner(String jarPath, Executor executor, Optional<String> subcommand) {
        this.jarPath = jarPath;
        this.executor = executor;
        this.maybeSubcommand = subcommand;
    }

    @Override
    public Future<CliExecutionResult> start(List<String> vmArgs, List<String> args) {
        return null;
    }

    @Override
    public CliExecutionResult run(List<String> args) {
        return run(List.of(), args);
    }

    @Override
    public CliExecutionResult run(List<String> vmParams, List<String> args) {
        return runJar(vmParams, args, DONT_BLOCK, NOOP_CONSUMER);
    }

    @Override
    public CliExecutionResult runSuccessfully(List<String> args) {
        return runSuccessfully(List.of(), args);
    }

    @Override
    public CliExecutionResult runSuccessfully(List<String> vmParams, List<String> args) {
        var cliRunResult = run(vmParams, args);
        if (cliRunResult.exitCode() != 0) {
            throw new NonZeroExitCodeException(cliRunResult);
        }
        return cliRunResult;
    }

    private ProcessBuilder getJavaProcessBuilder() {
        var processBuilder = new ProcessBuilder();
        processBuilder.environment().put("JAVA_HOME", JAVA_HOME);
        return processBuilder;
    }

    private CliExecutionResult runJar(List<String> vmParams,
                                      List<String> appArgs,
                                      Function<String, Boolean> blockUntilTrue,
                                      Consumer<String> stdoutLineConsumer) {
        countDownLatch = new CountDownLatch(1);
        var cmd = new ArrayList<String>();
        cmd.add(JAVA_BIN);
//        cmd.add("-agentlib:jdwp=transport=dt_socket,address=*:8831,server=y,suspend=y");
        cmd.addAll(vmParams);
        cmd.add("-jar");
        cmd.add(jarPath);
        maybeSubcommand.ifPresent(cmd::add);
        cmd.addAll(appArgs);

        var command = getJavaProcessBuilder().command(cmd);
        command.environment().put("JAVA_HOME", "/home/bartosz/.jdks/openjdk-18.0.1");
        return runJarProcess(blockUntilTrue, stdoutLineConsumer, cmd, command);
    }

    private CliExecutionResult runJarProcess(Function<String, Boolean> blockUntilTrue, Consumer<String> stdoutLineConsumer, ArrayList<String> cmd, ProcessBuilder command) {
        StringBuilder outputRead = new StringBuilder();
        final Process process;
        try {
            process = command.start();
            var reader = process.inputReader(StandardCharsets.UTF_8);

            readOutputWhileRunning(blockUntilTrue, stdoutLineConsumer, outputRead, process, reader);
            countDownLatch.countDown();

            var exitCode = process.exitValue();


            var stdout = outputRead
                    .append(reader.lines().collect(Collectors.joining("\n")))
                    .append("\n")
                    .toString();
            System.out.println(" ->>> " + stdout);
            return new CliExecutionResult(
                    cmd,
                    stdout,
                    new String(process.getErrorStream().readAllBytes()),
                    exitCode);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void readOutputWhileRunning(Function<String, Boolean> blockUntilTrue, Consumer<String> stdoutLineConsumer, StringBuilder outputRead, Process process, BufferedReader reader) throws IOException, InterruptedException {
        while (true) {
            if (blockUntilTrue != null) {
                while (true) {
                    if (!reader.ready()) {
                        break;
                    }

                    var line = reader.readLine();
                    if (line != null) {
                        outputRead.append(line).append("\n");
                        stdoutLineConsumer.accept(line);
                        if (blockUntilTrue.apply(line)) {
                            countDownLatch.countDown();
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
            var exited = process.waitFor(50, TimeUnit.MILLISECONDS);
            if (exited) {
                break;
            }
        }
    }
}