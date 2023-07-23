package beanvest.acceptance;

import beanvest.BeanvestMain;
import beanvest.lib.testing.AppRunner;
import beanvest.lib.testing.AppRunnerFactory;
import beanvest.lib.testing.CliExecutionResult;

import java.util.Arrays;
import java.util.List;

import static beanvest.lib.testing.asserts.AssertCliExecutionResult.assertExecution;
import static org.assertj.core.api.Assertions.assertThat;

public class BeanvestDsl {
    public BeanvestDsl() {
        appRunner = AppRunnerFactory.createRunner(BeanvestMain.class);
    }

    private final AppRunner appRunner;
    private CliExecutionResult cliRunResult;

    public void run() {
        cliRunResult = appRunner.run(List.of(), List.of());
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