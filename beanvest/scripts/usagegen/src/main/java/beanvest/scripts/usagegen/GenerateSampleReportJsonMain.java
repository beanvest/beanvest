package beanvest.scripts.usagegen;

import beanvest.BeanvestMain;
import beanvest.lib.apprunner.ReflectionRunner;
import beanvest.scripts.usagegen.generatessamplejson.SampleJsonWriter;
import beanvest.scripts.usagegen.generateusagedoc.ExampleRunner;
import beanvest.scripts.usagegen.generateusagedoc.ExampleRunner.Example;

import java.nio.file.Path;
import java.util.Optional;

public class GenerateSampleReportJsonMain {
    public static void main(String[] args) {
        var projectDir = Path.of(System.getProperty("project.dir"));
        var samplesDir = Path.of(projectDir + "/sample");
        var outputDir = Path.of(projectDir + "/generated/outputJson/");

        try (var runner = new ReflectionRunner(BeanvestMain.class, Optional.empty())) {
            var exampleRunner = new ExampleRunner(runner, samplesDir);

            var examples  = GenerateUsageDocMain.getUsageExamples()
                    .stream()
                    .map(e -> new Example(e.command() + " --json", e.description()))
                    .toList();

            var content = exampleRunner.generate(examples);
            var exampleDocWriter = new SampleJsonWriter(outputDir);
            exampleDocWriter.writeJson(content);
        }
    }
}
