package bb.scripts;

import bb.scripts.generateexamples.ExampleRunner;
import bb.scripts.generateexamples.ExampleRunner.Example;
import bb.scripts.generateexamples.UsageDocWriter;

import java.io.IOException;
import java.util.List;

public class GenerateExamplesMain {
    private static final String BEANVEST_BIN = "ledger/beanvest/build/native/nativeCompile/beanvest";
    private static final List<Example> EXAMPLES = List.of(
            new Example(
                    BEANVEST_BIN + " returns sample/* --group",
                    "Print various stats for all accounts and groups on each level of the accounts for whole period"),
            new Example(
                    BEANVEST_BIN + " returns sample/* --group --columns deps,wths --interval=quarter",
                    "Print cumulative deposits and withdrawals for accounts and groups for each quarter"),
            new Example(
                    BEANVEST_BIN + " journal sample/* | tail -n 20",
                    "Inspect journals with daily cumulative stats")
    );

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("<OUTPUT_FILE> argument required");
            System.exit(1);
        }
        var outputFile = args[0];

        var exampleDocWriter = new UsageDocWriter(outputFile);
        var exampleRunner = new ExampleRunner();
        var content = exampleRunner.generate(EXAMPLES);
        exampleDocWriter.writeDoc(content);
    }
}
