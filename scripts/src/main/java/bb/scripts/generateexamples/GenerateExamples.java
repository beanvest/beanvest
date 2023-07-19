package bb.scripts.generateexamples;

import bb.scripts.generateexamples.ExampleRunner.Example;

import java.io.IOException;
import java.util.List;

public class GenerateExamples {
    private final UsageDocWriter usageDocWriter;

    public GenerateExamples(UsageDocWriter usageDocWriter) {
        this.usageDocWriter = usageDocWriter;
    }

    public void run(List<Example> examples) throws IOException {
        var exampleRunner = new ExampleRunner();
        var content = exampleRunner.generate(examples);
        usageDocWriter.writeDoc(content);
    }
}
