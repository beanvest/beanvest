package beanvest.scripts.generateusagedoc;

import beanvest.scripts.generateusagedoc.ExampleRunner.ExampleWithOutput;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UsageDocWriter {
    private final Path outputFile;
    private final Path samplesDir;

    public UsageDocWriter(Path outputFile, Path samplesDir) {
        this.outputFile = outputFile;
        this.samplesDir = samplesDir.getFileName();
    }

    public void writeDoc(List<ExampleWithOutput> examples) throws IOException {
        var stringBuffer = new StringBuffer();
        stringBuffer.append("## Usage examples\n\n");
        examples.forEach(example -> stringBuffer
                .append("""
                        - %s
                          ```bash
                          beanvest %s
                          ```
                          ```
                        %s
                          ```
                        """
                        .formatted(
                                example.example().description(),
                                example.example().command().replace("$samplesDir$", samplesDir.toString()),
                                indent(example.commandOutput(), 2))));
        Files.writeString(outputFile, stringBuffer);
    }

    private static String indent(String content, int spaces) {
        var indent = " ".repeat(spaces);
        return Arrays.stream(content.split("\n"))
                .map(line -> indent + line)
                .collect(Collectors.joining("\n"));
    }
}
