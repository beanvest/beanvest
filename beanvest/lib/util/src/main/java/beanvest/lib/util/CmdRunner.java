package beanvest.lib.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class CmdRunner {
    private final File cwd;

    public CmdRunner(Path cwd) {
        this.cwd = cwd.toAbsolutePath().toFile();
    }

    public CmdResult runSuccessfully(List<String> command) throws IOException, InterruptedException {

        var process = new ProcessBuilder()
                .command(command)
                .directory(cwd)
                .start();

        var stdOut = readWholeProcessOutput(process.getInputStream(), "stdOut thread");
        var stdErr = readWholeProcessOutput(process.getErrorStream(), "stdErr thread");

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Process `%s` returned exit code %d. \nStderr: %s\nStdout: %s".formatted(command, exitCode, stdOut, stdErr));
        }
        return new CmdResult(command, stdOut, stdErr, exitCode);
    }

    private static String readWholeProcessOutput(InputStream stream, String threadName) {
        var stdOut = new AtomicReference<String>();
        var threadOut = new Thread(() -> {
            try {
                stdOut.set(new String(stream.readAllBytes(), StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        threadOut.setName(threadName);
        threadOut.start();

        return stdOut.get();
    }

}
