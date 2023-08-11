package beanvest.scripts;

import beanvest.BeanvestMain;
import beanvest.lib.apprunner.AppRunnerFactory;
import beanvest.scripts.generatessamplejson.SampleJsonWriter;
import beanvest.scripts.generateusagedoc.ExampleRunner;
import beanvest.scripts.generateusagedoc.ExampleRunner.Example;

import java.io.IOException;
import java.nio.file.Path;

public class GenerateSampleJsonMain {
    public static void main(String[] args) throws IOException {
        var projectDir = Path.of(System.getProperty("project.dir"));
        var samplesDir = Path.of(projectDir + "/sample");
        var outputDir = Path.of(projectDir + "/generated/outputJson/");

        var runner = AppRunnerFactory.createRunner(BeanvestMain.class);
        var exampleRunner = new ExampleRunner(runner, samplesDir);

        var examples = GenerateUsageDocMain.EXAMPLES
                .stream()
                .map(e -> new Example(e.command() + " --json", e.description()))
                .toList();
        var content = exampleRunner.generate(examples);
        var exampleDocWriter = new SampleJsonWriter(outputDir);
        exampleDocWriter.writeJson(content);
    }
}
