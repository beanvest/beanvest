package beanvest.acceptance;

import beanvest.BeanvestMain;
import beanvest.lib.apprunner.AppRunner;
import beanvest.lib.apprunner.AppRunnerFactory;
import beanvest.lib.apprunner.CliExecutionResult;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RunnerTest {

    @Test
    void cashOperationsHaveSameResult() {
        var report = AppRunnerFactory.createRunner(BeanvestMain.class);
        var report1 = report.run(List.of("report",
                "/home/bartosz/Projects/ledger/generated/beanvest_import/properties",
                "--currency=PLN",
                "--columns=dw,value,again,xirr",
                "--end=month"));
        System.out.println(report1.stdErr());
        System.out.println(report1.stdOut());
        assertEquals(0, report1.exitCode());
    }
}

