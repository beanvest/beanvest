package beanvest.scripts.usagegen;

import beanvest.BeanvestMain;
import beanvest.lib.apprunner.AppRunnerFactory;
import beanvest.scripts.usagegen.generateusagedoc.ExampleRunner;
import beanvest.scripts.usagegen.generateusagedoc.ExampleRunner.Example;
import beanvest.scripts.usagegen.generateusagedoc.UsageDocWriter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class GenerateUsageDocMain {
    public static final List<Example> EXAMPLES = List.of(
            new Example(
                    "returns $samplesDir$ --end=2023-07-01 --columns=Deps,Wths,Div,Intr,Fees,Value,Cost,Profit,rgain,ugain",
                    "Print various stats for all accounts and groups on each level of the accounts for whole period"),
            new Example(
                    "returns $samplesDir$ --end=2023-07-01 --columns=Deps,Wths,Value,Cost,Profit --report-holdings",
                    "Print cash stats on holdings, accounts and groups"),
            new Example(
                    "returns $samplesDir$ --end=2023-07-01 --columns deps,wths --interval=quarter",
                    "Print cumulative deposits and withdrawals for accounts and groups for each quarter"),
            new Example(
                    "returns $samplesDir$ --end=2023-07-01 --columns dw --interval=quarter --delta",
                    "Print changes in deposits+withdrawals in each period for accounts and groups quarterly")
    );

    public static void main(String[] args) throws IOException {
        var projectDir = Path.of(System.getProperty("project.dir"));
        var sampleDirStr = System.getProperty("sample.dir");
        var samplesDir = sampleDirStr != null
                ? Path.of(sampleDirStr)
                : Path.of(projectDir + "/sample");
        var outputFile = Path.of(projectDir + "/generated/usage.md");

        try (var runner = AppRunnerFactory.createRunner(BeanvestMain.class)) {
            var exampleRunner = new ExampleRunner(runner, samplesDir);
            var exampleDocWriter = new UsageDocWriter(outputFile, samplesDir);

            var content = exampleRunner.generate(EXAMPLES);
            exampleDocWriter.writeDoc(content);
        }
    }
}
