package beanvest.acceptance.export;

import beanvest.BeanvestMain;
import beanvest.lib.testing.AppRunner;
import beanvest.lib.testing.AppRunnerFactory;
import beanvest.lib.testing.TestFiles;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AutoClosingSubAccountsAcceptanceTest {
    protected AppRunner runner = AppRunnerFactory.createRunner(BeanvestMain.class, "export");

    @Test
    void autoClosingSubAccounts() {
        var path = TestFiles.writeToTempFile("""
                account Trading
                currency GBP
                                
                2022-02-02 deposit and buy 1 VLS for 10
                2022-02-03 sell 0.5 VLS for 6
                2022-02-04 sell 0.5 VLS for 6
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
                
                2022-02-03 txn "sell"
                  Trading:Cash  6 GBP
                  Trading:VLS  -0.5 VLS {10.0000000000 GBP} @ 12.0000000000 GBP
                  Income:Gains:Shares
                
                2022-02-04 txn "sell"
                  Trading:Cash  6 GBP
                  Trading:VLS  -0.5 VLS {10.0000000000 GBP} @ 12.0000000000 GBP
                  Income:Gains:Shares
                
                2022-02-04 close Trading:VLS

                """, CliExecutionResult.stdOut());
    }

    @Test
    void dontCloseAccountOfSoldCommodityIfItsTradedLater() {
        var path = TestFiles.writeToTempFile("""
                account Trading
                currency GBP
                2022-02-02 deposit 20
                2022-02-02 buy 1 VLS for 10
                2022-02-03 sell 1 VLS for 12
                2022-02-04 buy 1 VLS for 10
                """);

        var CliExecutionResult = runner.runSuccessfully(List.of(path.toString()));

        assertEquals("""
                2022-02-02 open Trading:Cash
                                
                2022-02-02 txn "deposit"
                  Equity:Bank
                  Trading:Cash  20 GBP
                                
                2022-02-02 open Trading:VLS
                                
                2022-02-02 txn "buy"
                  Trading:Cash  -10 GBP
                  Trading:VLS  1 VLS {10.0000000000 GBP} @@ 10 GBP
                                
                2022-02-03 txn "sell"
                  Trading:Cash  12 GBP
                  Trading:VLS  -1 VLS {10.0000000000 GBP} @ 12.0000000000 GBP
                  Income:Gains:Shares
                                  
                2022-02-04 txn "buy"
                  Trading:Cash  -10 GBP
                  Trading:VLS  1 VLS {10.0000000000 GBP} @@ 10 GBP
                                
                """, CliExecutionResult.stdOut());
    }
}

