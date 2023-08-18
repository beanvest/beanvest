package beanvest.lib.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CmdRunner {
    private final File cwd;
    private final ExecutorService execService = Executors.newFixedThreadPool(2);

    public CmdRunner(Path cwd) {
        this.cwd = cwd.toAbsolutePath().toFile();
    }

    public CmdResult runSuccessfully(List<String> command) {
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

            if (exitCode != 0) {
                throw new RuntimeException("Process `%s` returned exit code %d. \nStderr: %s\nStdout: %s".formatted(command, exitCode, stdOutString, stdErrString));
            }

            return new CmdResult(command, stdOutString, stdErrString, exitCode);
        } catch (ExecutionException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
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

}
