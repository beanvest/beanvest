package beanvest.lib.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CmdRunner implements AutoCloseable {
    private final File cwd;
    private final ExecutorService execService = Executors.newFixedThreadPool(2);

    public CmdRunner(Path cwd) {
        this.cwd = cwd.toAbsolutePath().toFile();
    }

    public CmdRunner() {
        this.cwd = new File(System.getProperty("user.dir"));
    }

    public CmdResult runSuccessfully(List<String> command) {
        var cmdResult = run(command);

        if (cmdResult.exitCode() != 0) {
            throw new RuntimeException("Process `%s` returned exit code %d. \nStderr: %s\nStdout: %s"
                    .formatted(command, cmdResult.exitCode(), cmdResult.stdOut(), cmdResult.stdErr()));
        }
        return cmdResult;
    }

    public CmdResult run(List<String> command) {
        CmdResult cmdResult;
        try {
            var process = new ProcessBuilder()
                    .command(command)
                    .directory(cwd)
                    .start();

            var stdOut = readWholeProcessOutput(process.getInputStream());
            var stdErr = readWholeProcessOutput(process.getErrorStream());

            int exitCode = process.waitFor();
            var stdOutString = stdOut.get();
            var stdErrString = stdErr.get();

            cmdResult = new CmdResult(command, stdOutString, stdErrString, exitCode);
        } catch (ExecutionException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return cmdResult;
    }

    private Future<String> readWholeProcessOutput(InputStream stream) {
        return execService.submit(() -> {
            try {
                return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void close() {
        execService.shutdown();
    }
}
