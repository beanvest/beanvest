package beanvest.test.export.acceptance;

import beanvest.BeanvestMain;
import beanvest.lib.testing.AppRunner;
import beanvest.lib.testing.AppRunnerFactory;
import beanvest.lib.testing.TestFiles;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DividendsAcceptanceTest {
    protected AppRunner runner = AppRunnerFactory.createRunner(BeanvestMain.class, "export");

    @Test
    void generatesDividendTransactions() {
        var path = TestFiles.writeToTempFile("""
                account Trading
                currency GBP
                                
                2022-02-02 deposit and buy 1 VLS for 10
                2022-02-03 dividend 2 from VLS
                """);

        var CliExecutionResult = runner.runSuccessfully(List.of(path.toString()));

        assertEquals("""
                2022-02-02 open Trading:Cash
                 
                2022-02-02 txn "deposit"
                  Equity:Bank
                  Trading:Cash  10 GBP
                
                2022-02-02 open Trading:VLS
                
                2022-02-02 txn "buy"
                  Trading:Cash  -10 GBP
                  Trading:VLS  1 VLS {10.0000000000 GBP} @@ 10 GBP
                  
                2022-02-03 txn "dividend"
                  Income:Dividends
                  Trading:Cash  2 GBP

                """, CliExecutionResult.stdOut());
    }
}

