package beanvest.scripts.usagegen;

import beanvest.BeanvestMain;
import beanvest.lib.apprunner.ReflectionRunner;
import beanvest.scripts.usagegen.generateusagedoc.ExampleRunner;
import beanvest.scripts.usagegen.generateusagedoc.ExampleRunner.Example;
import beanvest.scripts.usagegen.generateusagedoc.UsageDocWriter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GenerateUsageDocMain {
    private static final ExampleVarReplacer exampleVarReplacer = new ExampleVarReplacer();
    private static final String END = "--end=2023-07-01";
    private static final String START_YEARLY = "--startDate=2019-01-01";
    private static final String START_QUARTERLY = "--startDate=2022-07-01";
    private static final String START_MONTHLY = "--startDate=2023-01-01";
    private static final List<Example> EXAMPLES = List.of(
            new Example(
                    "report $samplesDir$ $end$ --columns again,xirr --interval=year $startYear$ --delta",
                    "Print gains and returns of each year"),
            new Example(
                    "report $samplesDir$ $end$ --columns again,xirr --interval=year $startYear$",
                    "Print cumulative gains and total return of trading accounts after each year"),
            new Example(
                    "report $samplesDir$ $end$ --columns Deps,Wths,Div,Intr,Fees,Value,rgain,ugain",
                    "Print various stats for all accounts and groups on each level of the accounts"),
            new Example(
                    "report $samplesDir$ $end$ --columns Deps,Wths,Value,again,xirr --report-holdings",
                    "Print cash stats on holdings, accounts and groups"),
            new Example(
                    "report $samplesDir$ $end$ --columns value,again --interval=quarter $startQuarter$",
                    "Print value of the accounts and total gains quarterly"),
            new Example(
                    "report $samplesDir$ $end$ --columns dw --interval=month $startMonth$ --delta",
                    "Print monthly net deposits (deposits-withdrawals)"),
            new Example(
                    "report $samplesDir$ $end$ --columns dw,value --interval=month $startMonth$ --delta --currency PLN",
                    "Print monthly net deposits and changes in value converted to other currency")

    );

    public static List<Example> getUsageExamples()
    {
        Map<String, String> replacements = Map.of(
                "$end$", END,
                "$startMonth$", START_MONTHLY,
                "$startQuarter$", START_QUARTERLY,
                "$startYear$", START_YEARLY
        );
        return exampleVarReplacer.resolveVars(replacements, EXAMPLES);
    }

    public static void main(String[] args) throws IOException {
        var projectDir = Path.of(System.getProperty("project.dir"));
        var sampleDirStr = System.getProperty("sample.dir");
        var samplesDir = (sampleDirStr != null
                ? Path.of(sampleDirStr)
                : Path.of(projectDir + "/sample")).toAbsolutePath();
        var outputFile = Path.of(projectDir + "/generated/usage.md");

        try (var runner = new ReflectionRunner(BeanvestMain.class, Optional.empty())) {
            var exampleRunner = new ExampleRunner(runner, samplesDir);
            var exampleDocWriter = new UsageDocWriter(outputFile, samplesDir);

            var content = exampleRunner.generate(getUsageExamples());
            exampleDocWriter.writeDoc(content);
        }
    }
}
