package beanvest.scripts.generatessamplejson;

import beanvest.lib.util.gson.GsonFactory;
import beanvest.scripts.generateusagedoc.ExampleRunner.ExampleWithOutput;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SampleJsonWriter {
    private final Path outputDir;

    public SampleJsonWriter(Path outputDir) {
        this.outputDir = outputDir;
    }

    public void writeJson(List<ExampleWithOutput> examples) {
        try {
            var gson = new GsonBuilder().setPrettyPrinting().create();
            for (int i = 0; i < examples.size(); i++) {
                ExampleWithOutput example = examples.get(i);
                var jsonString = example.commandOutput();
                var jsonTree = JsonParser.parseString(jsonString);
                var prettyPrintedJson = gson.toJson(jsonTree);
                Files.writeString(Path.of(outputDir + "/sample%d.json".formatted(i + 1)), prettyPrintedJson);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String indent(String content, int spaces) {
        var indent = " ".repeat(spaces);
        return Arrays.stream(content.split("\n"))
                .map(line -> indent + line)
                .collect(Collectors.joining("\n"));
    }
}
