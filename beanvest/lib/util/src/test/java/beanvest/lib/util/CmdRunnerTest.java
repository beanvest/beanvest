package beanvest.lib.util;

import beanvest.lib.testing.TestFiles;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CmdRunnerTest {

    @Test @Disabled
    void testBigOutput() throws IOException, InterruptedException {
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            content.append("This is a test line\n");
        }
        var file = TestFiles.writeToTempFile(content.toString());

        var command = new CmdRunner(Path.of("/"));

        command.runSuccessfully(List.of("cat", file.toAbsolutePath().toString()));
    }

    @Test @Disabled
    void testBigStdOutput() throws IOException, InterruptedException {
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            content.append("This is a test line\n");
        }
        var file = TestFiles.writeToTempFile(content.toString());

        var command = new CmdRunner(Path.of("/"));

        command.runSuccessfully(List.of("bash",  "-c", "cat \"%s\" 1>&2".formatted(file.toAbsolutePath().toString())));
    }

}