package beanvest.lib.testing.apprunner;

import beanvest.lib.testing.AppRunner;
import beanvest.lib.testing.CliExecutionResult;
import beanvest.lib.testing.NonZeroExitCodeException;

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


public class NativeBinRunner implements AppRunner {
    public static final String JAVA_HOME = System.getProperty("java.home");
    public static final String JAVA_BIN = JAVA_HOME + "/bin/java";
    public static final Function<String, Boolean> DONT_BLOCK = line -> true;
    public static final Consumer<String> NOOP_CONSUMER = line -> {
    };
    private final String binaryPath;
    private final Optional<String> maybeSubcommand;

    private CountDownLatch waitingForOutputLatch;

    public NativeBinRunner(String binaryPath, Optional<String> subcommand) {
        this.binaryPath = binaryPath;
        this.maybeSubcommand = subcommand;
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
        waitingForOutputLatch = new CountDownLatch(1);
        var cmd = new ArrayList<String>();
//        cmd.add("-agentlib:jdwp=transport=dt_socket,address=*:8831,server=y,suspend=y");
        cmd.add(binaryPath);
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
            waitingForOutputLatch.countDown();

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
                            waitingForOutputLatch.countDown();
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