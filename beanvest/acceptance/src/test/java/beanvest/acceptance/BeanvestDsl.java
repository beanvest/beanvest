package beanvest.acceptance;

import beanvest.BeanvestMain;
import beanvest.acceptance.report.dsl.BeanvestRunner;
import beanvest.lib.apprunner.AppRunner;
import beanvest.lib.apprunner.CliExecutionResult;

import java.util.Arrays;
import java.util.List;

import static beanvest.lib.testing.asserts.AssertCliExecutionResult.assertExecution;
import static org.assertj.core.api.Assertions.assertThat;

public class BeanvestDsl {
    public BeanvestDsl() {
        appRunner = BeanvestRunner.createRunner(BeanvestMain.class);
    }

    public void close()
    {
        appRunner.close();
    }

    private final BeanvestRunner appRunner;
    private CliExecutionResult cliRunResult;

    public void run() {
        cliRunResult = appRunner.run(List.of());
    }

    public void verifyHasPrintedUsage() {
        assertExecution(cliRunResult)
                .hasPrintedUsage();
    }

    public void verifyHasPrintedSubcommands(String commands) {
        var list = Arrays.stream(commands.split(", ")).toList();
        assertExecution(cliRunResult)
                .hasPrintedCommands(list);
    }

    public void verifyHasNotFinishedSuccessfully() {
        assertThat(cliRunResult.exitCode())
                .isNotZero();
    }
}