package beanvest.scripts.usagegen;

import beanvest.BeanvestMain;
import beanvest.lib.apprunner.AppRunnerFactory;
import beanvest.scripts.usagegen.generatessamplejson.SampleJsonWriter;
import beanvest.scripts.usagegen.generateusagedoc.ExampleRunner;
import beanvest.scripts.usagegen.generateusagedoc.ExampleRunner.Example;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class GenerateSampleOptionsMain {
    public static void main(String[] args) throws IOException {
        var outputPath = System.getProperty("generated.dir") + "/options.json";

        var runner = AppRunnerFactory.createRunner(BeanvestMain.class);
        var exampleRunner = new ExampleRunner(runner, Path.of(""));

        var options = new Example("options", "prints all options for the UIs in parsable format");
        var content = exampleRunner.generate(List.of(options));
        SampleJsonWriter.writeJson(content.get(0), outputPath);
    }
}
