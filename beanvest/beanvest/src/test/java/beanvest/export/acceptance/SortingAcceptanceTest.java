package beanvest.export.acceptance;

import bb.lib.testing.AppRunner;
import bb.lib.testing.AppRunnerFactory;
import bb.lib.testing.TestFiles;
import beanvest.BeanvestMain;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SortingAcceptanceTest {
    protected AppRunner runner = AppRunnerFactory.createRunner(BeanvestMain.class, "export");

    @Test
    void orderOfTransactionsOnOneDayIsPreserved() {
        var path = TestFiles.writeToTempFile("""
                account Assets:VanguardTaxable
                currency GBP
                                
                2022-02-02 deposit 100
                2022-02-02 buy 2 VGB for 20 "a"
                2022-02-02 buy 2 VGB for 20 "b"
                2022-02-02 buy 2 VGB for 20 "c"
                2022-02-02 buy 2 VGB for 20 "d"
                2022-02-02 buy 2 VGB for 20 "e"
                """);

        var CliExecutionResult = runner.runSuccessfully(List.of(path.toString()));

        assertEquals("""
                2022-02-02 open Assets:VanguardTaxable:Cash

                2022-02-02 txn "deposit"
                  Equity:Bank
                  Assets:VanguardTaxable:Cash  100 GBP

                2022-02-02 open Assets:VanguardTaxable:VGB

                2022-02-02 txn "buy - a"
                  Assets:VanguardTaxable:Cash  -20 GBP
                  Assets:VanguardTaxable:VGB  2 VGB {10.0000000000 GBP} @@ 20 GBP

                2022-02-02 txn "buy - b"
                  Assets:VanguardTaxable:Cash  -20 GBP
                  Assets:VanguardTaxable:VGB  2 VGB {10.0000000000 GBP} @@ 20 GBP

                2022-02-02 txn "buy - c"
                  Assets:VanguardTaxable:Cash  -20 GBP
                  Assets:VanguardTaxable:VGB  2 VGB {10.0000000000 GBP} @@ 20 GBP

                2022-02-02 txn "buy - d"
                  Assets:VanguardTaxable:Cash  -20 GBP
                  Assets:VanguardTaxable:VGB  2 VGB {10.0000000000 GBP} @@ 20 GBP

                2022-02-02 txn "buy - e"
                  Assets:VanguardTaxable:Cash  -20 GBP
                  Assets:VanguardTaxable:VGB  2 VGB {10.0000000000 GBP} @@ 20 GBP

                """, CliExecutionResult.stdOut());
    }

    @Test
    void transactionsAreOrderedByDate() {
        var path = TestFiles.writeToTempFile("""
                account Assets:VanguardTaxable
                currency GBP
                                
                2022-02-01 deposit 100
                2022-02-05 buy 2 VGB for 20 "e"
                2022-02-04 buy 2 VGB for 20 "d"
                2022-02-03 buy 2 VGB for 20 "c"
                2022-02-02 buy 2 VGB for 20 "b"
                2022-02-01 buy 2 VGB for 20 "a"
                """);

        var CliExecutionResult = runner.runSuccessfully(List.of(path.toString()));

        assertEquals("""
                2022-02-01 open Assets:VanguardTaxable:Cash

                2022-02-01 txn "deposit"
                  Equity:Bank
                  Assets:VanguardTaxable:Cash  100 GBP

                2022-02-01 open Assets:VanguardTaxable:VGB

                2022-02-01 txn "buy - a"
                  Assets:VanguardTaxable:Cash  -20 GBP
                  Assets:VanguardTaxable:VGB  2 VGB {10.0000000000 GBP} @@ 20 GBP

                2022-02-02 txn "buy - b"
                  Assets:VanguardTaxable:Cash  -20 GBP
                  Assets:VanguardTaxable:VGB  2 VGB {10.0000000000 GBP} @@ 20 GBP

                2022-02-03 txn "buy - c"
                  Assets:VanguardTaxable:Cash  -20 GBP
                  Assets:VanguardTaxable:VGB  2 VGB {10.0000000000 GBP} @@ 20 GBP

                2022-02-04 txn "buy - d"
                  Assets:VanguardTaxable:Cash  -20 GBP
                  Assets:VanguardTaxable:VGB  2 VGB {10.0000000000 GBP} @@ 20 GBP

                2022-02-05 txn "buy - e"
                  Assets:VanguardTaxable:Cash  -20 GBP
                  Assets:VanguardTaxable:VGB  2 VGB {10.0000000000 GBP} @@ 20 GBP

                """, CliExecutionResult.stdOut());
    }

    @Test
    void transactionsAreOrderedByInclusionOrder() {
        var path = TestFiles.writeToTempFile("""
                account Assets:Z
                currency GBP
                                
                2022-02-01 deposit 100 "a"
                """);
        var path2 = TestFiles.writeToTempFile("""
                account Assets:B
                currency GBP
                                
                2022-02-01 deposit 100 "b"
                """);

        var CliExecutionResult = runner.runSuccessfully(List.of(path.toString(), path2.toString()));

        assertEquals("""
                2022-02-01 open Assets:Z:Cash
                            
                2022-02-01 txn "deposit - a"
                  Equity:Bank
                  Assets:Z:Cash  100 GBP
                        
                2022-02-01 open Assets:B:Cash
                            
                2022-02-01 txn "deposit - b"
                  Equity:Bank
                  Assets:B:Cash  100 GBP

                    """, CliExecutionResult.stdOut());
    }
}

