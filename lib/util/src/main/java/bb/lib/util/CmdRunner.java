package bb.lib.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class CmdRunner {
    private final File cwd;

    public CmdRunner() {
        this.cwd = new File(System.getProperty("user.dir")); // use jdk's cwd
    }

    public CmdResult runSuccessfully(List<String> command) throws IOException, InterruptedException {
        var process = new ProcessBuilder()
                .command(command)
                .directory(cwd)
                .start();

        int exitCode = process.waitFor();

        var stdOut = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        var stdErr = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);

        if (exitCode != 0) {
            throw new RuntimeException("Process `%s` returned exit code %d. \nStderr: %s\nStdout: %s".formatted(command, exitCode, stdOut, stdErr));
        }
        return new CmdResult(command, stdOut, stdErr, exitCode);
    }
}
