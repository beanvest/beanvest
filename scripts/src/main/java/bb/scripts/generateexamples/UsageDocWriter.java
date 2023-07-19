package bb.scripts.generateexamples;

import bb.scripts.generateexamples.ExampleRunner.ExampleWithOutput;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UsageDocWriter {
    private final String outputFile;

    public UsageDocWriter(String outputFile) {

        this.outputFile = outputFile;
    }

    public void writeDoc(List<ExampleWithOutput> examples) throws IOException {
        var stringBuffer = new StringBuffer();
        stringBuffer.append("## Usage examples\n\n");
        examples.forEach(example -> stringBuffer
                .append("""
                        - %s
                          ```
                          %s
                          ```
                          ```
                        %s
                          ```
                        """
                        .formatted(
                                example.example().description(),
                                example.example().command(),
                                indent(example.commandOutput(), 2))));
        Files.writeString(Path.of(outputFile), stringBuffer);
    }

    private static String indent(String content, int spaces) {
        var indent = " ".repeat(spaces);
        return Arrays.stream(content.split("\n"))
                .map(line -> indent + line)
                .collect(Collectors.joining("\n"));
    }
}
