package beanvest.lib.util;

import beanvest.lib.testing.TestFiles;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

class CmdRunnerTest {

    @Test
    void testBigStdOutput() throws IOException, InterruptedException {
        var file = TestFiles.writeToTempFile("This is a test line\n".repeat(10000));

        var command = new CmdRunner(Path.of("/"));

        command.runSuccessfully(List.of("cat", file.toAbsolutePath().toString()));
    }

    @Test
    void testBigStdError() throws IOException, InterruptedException {
        var file = TestFiles.writeToTempFile("This is a test line\n".repeat(10000));

        var command = new CmdRunner(Path.of("/"));

        command.runSuccessfully(List.of("bash",  "-c", "cat \"%s\" 1>&2".formatted(file.toAbsolutePath().toString())));
    }

}