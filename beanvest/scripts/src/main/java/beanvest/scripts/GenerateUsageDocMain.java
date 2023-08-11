package beanvest.scripts;

import beanvest.BeanvestMain;
import beanvest.lib.apprunner.AppRunnerFactory;
import beanvest.scripts.generateusagedoc.ExampleRunner;
import beanvest.scripts.generateusagedoc.ExampleRunner.Example;
import beanvest.scripts.generateusagedoc.UsageDocWriter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class GenerateUsageDocMain {
    private static final List<Example> EXAMPLES = List.of(
            new Example(
                    "returns $samplesDir$ --end=2023-07-01 --columns=Deps,Wths,Div,Intr,Fees,Val,Cost,Profit",
                    "Print various stats for all accounts and groups on each level of the accounts for whole period"),
            new Example(
                    "returns $samplesDir$ --end=2023-07-01 --columns=Deps,Wths,Val,Cost,Profit --report-holdings",
                    "Print cash stats on holdings, accounts and groups"),
            new Example(
                    "returns $samplesDir$ --columns deps,wths --interval=quarter",
                    "Print cumulative deposits and withdrawals for accounts and groups for each quarter")
//            new Example(
//                    "returns $samplesDir$ --columns deps,wths --interval=quarter --delta",
//                    "Print changes in deposits and withdrawals for accounts and groups for each quarter")
    );

    public static void main(String[] args) throws IOException {
        var projectDir = Path.of(System.getProperty("project.dir"));
        var samplesDir = Path.of(projectDir + "/sample");
        var outputFile = Path.of(projectDir + "/generated/usage.md");

        var runner = AppRunnerFactory.createRunner(BeanvestMain.class);
        var exampleRunner = new ExampleRunner(runner, samplesDir);
        var exampleDocWriter = new UsageDocWriter(outputFile, samplesDir);

        var content = exampleRunner.generate(EXAMPLES);
        exampleDocWriter.writeDoc(content);
    }
}
