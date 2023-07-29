package beanvest.acceptance.importer;

import beanvest.BeanvestMain;
import beanvest.lib.apprunner.AppRunner;
import beanvest.lib.apprunner.AppRunnerFactory;
import beanvest.lib.testing.TestFiles;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SortingAcceptanceTest {
    protected AppRunner runner = AppRunnerFactory.createRunner(BeanvestMain.class, "import");

    @Test
    void importAssetsTransactionsAsDepositsAndWithdrawals() {
        var path = TestFiles.writeToTempFile("""
                2022-01-01 open Assets:Property
                2022-01-01 open Equity:Bank
                                
                2022-02-10 * "Zakup"
                  Assets:Property    240000 PLN
                  Equity:Bank
                  
                2022-02-12 * "partial sell"
                  Assets:Property    -10000 PLN
                  Equity:Bank
                """);

        var CliExecutionResult = runner.runSuccessfully(List.of(path.toString(), "Property", "NewProp"));

        assertEquals("""
                        account NewProp
                        currency PLN
                                        
                        2022-02-10 deposit 240000 "Zakup"
                        2022-02-12 withdraw 10000 "partial sell"
                        """,
                CliExecutionResult.stdOut());
    }
}

