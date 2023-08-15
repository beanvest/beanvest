package beanvest.scripts.usagegen.generatessamplejson;

import beanvest.scripts.usagegen.generateusagedoc.ExampleRunner.ExampleWithOutput;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SampleJsonWriter {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Path outputDir;

    public SampleJsonWriter(Path outputDir) {
        this.outputDir = outputDir;
    }

    public void writeJson(List<ExampleWithOutput> examples) {
        try {
            for (int i = 0; i < examples.size(); i++) {
                ExampleWithOutput example = examples.get(i);
                var filename = "sample%d.json".formatted(i + 1);
                var outputPath = outputDir + "/" + filename;
                writeJson(example, outputPath);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeJson(ExampleWithOutput example, String outputPath) throws IOException {
        var jsonString = example.commandOutput();
        var jsonTree = JsonParser.parseString(jsonString);
        var prettyPrintedJson = GSON.toJson(jsonTree);
        Files.writeString(Path.of(outputPath), prettyPrintedJson);
    }
}
