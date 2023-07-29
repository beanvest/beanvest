package beanvest.scripts;

import beanvest.scripts.generateusagedoc.ExampleRunner;
import beanvest.scripts.generateusagedoc.ExampleRunner.Example;
import beanvest.scripts.generateusagedoc.UsageDocWriter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class GenerateUsageDocMain {
    private static final String BEANVEST_BIN = "beanvest/beanvest/build/native/nativeCompile/";
    private static final List<Example> EXAMPLES = List.of(
            new Example(
                    "beanvest returns sample/* --end=2023-07-01",
                    "Print various stats for all accounts and groups on each level of the accounts for whole period"),
            new Example(
                    "beanvest returns sample/* --columns deps,wths --interval=quarter",
                    "Print cumulative deposits and withdrawals for accounts and groups for each quarter"),
            new Example(
                    "beanvest returns sample/* --columns deps,wths --interval=quarter --delta",
                    "Print changes in deposits and withdrawals for accounts and groups for each quarter"),
            new Example(
                    "beanvest journal sample/* | tail -n 20",
                    "Inspect journals with daily cumulative stats")
    );

    public static void main(String[] args) throws IOException {
        var projectDir = Path.of(System.getProperty("project.dir"));
        if (args.length == 0) {
            System.out.println("<OUTPUT_FILE> argument required");
            System.exit(1);
        }

        var outputFile = Path.of(projectDir + "/" + args[0]);

        var exampleDocWriter = new UsageDocWriter(outputFile);
        var exampleRunner = new ExampleRunner(BEANVEST_BIN, projectDir);

        var content = exampleRunner.generate(EXAMPLES);
        exampleDocWriter.writeDoc(content);
    }
}
