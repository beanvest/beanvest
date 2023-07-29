package beanvest.acceptance.export;

import beanvest.BeanvestMain;
import beanvest.lib.apprunner.AppRunner;
import beanvest.lib.apprunner.AppRunnerFactory;
import beanvest.lib.testing.TestFiles;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExportAcceptanceTest {
    protected AppRunner runner = AppRunnerFactory.createRunner(BeanvestMain.class, "export");

    @Test
    void simpleCase() {
        var path = TestFiles.writeToTempFile("""
                account Assets:VanguardTaxable
                currency GBP
                                
                2022-02-02 deposit 42
                2022-02-02 buy 2 VGB for 20
                2022-02-03 buy 2 VGB for 22
                2022-02-04 sell 3 VGB for 33 "switch to SP500"
                2022-02-02 withdraw 33
                """);

        var CliExecutionResult = runner.runSuccessfully(List.of(path.toString()));

        assertEquals("""
                2022-02-02 open Assets:VanguardTaxable:Cash
                            
                2022-02-02 txn "deposit"
                  Equity:Bank
                  Assets:VanguardTaxable:Cash  42 GBP

                2022-02-02 open Assets:VanguardTaxable:VGB
                            
                2022-02-02 txn "buy"
                  Assets:VanguardTaxable:Cash  -20 GBP
                  Assets:VanguardTaxable:VGB  2 VGB {10.0000000000 GBP} @@ 20 GBP
                            
                2022-02-02 txn "withdraw"
                  Equity:Bank
                  Assets:VanguardTaxable:Cash  -33 GBP
                            
                2022-02-03 txn "buy"
                  Assets:VanguardTaxable:Cash  -22 GBP
                  Assets:VanguardTaxable:VGB  2 VGB {11.0000000000 GBP} @@ 22 GBP
                            
                2022-02-04 txn "sell - switch to SP500"
                  Assets:VanguardTaxable:Cash  33 GBP
                  Assets:VanguardTaxable:VGB  -2 VGB {10.0000000000 GBP} @ 11.0000000000 GBP
                  Assets:VanguardTaxable:VGB  -1 VGB {11.0000000000 GBP} @ 11.0000000000 GBP
                  Income:Gains:Shares
                  
                """, CliExecutionResult.stdOut());
    }

    @Test
    void commoditiesWithNumbers() {
        var path = TestFiles.writeToTempFile("""
                account Assets:VanguardTaxable
                currency GBP
                                
                2022-02-02 deposit 42
                2022-02-02 buy 2 VLS60 for 20
                """);

        var CliExecutionResult = runner.runSuccessfully(List.of(path.toString()));

        assertEquals("""
                2022-02-02 open Assets:VanguardTaxable:Cash
                            
                2022-02-02 txn "deposit"
                  Equity:Bank
                  Assets:VanguardTaxable:Cash  42 GBP

                2022-02-02 open Assets:VanguardTaxable:VLS60
                            
                2022-02-02 txn "buy"
                  Assets:VanguardTaxable:Cash  -20 GBP
                  Assets:VanguardTaxable:VLS60  2 VLS60 {10.0000000000 GBP} @@ 20 GBP
                  
                """, CliExecutionResult.stdOut());
    }

    @Test
    void withdrawals() {
        var path = TestFiles.writeToTempFile("""
                account Assets:VanguardTaxable
                currency GBP
                                
                2022-02-02 deposit 42
                2022-02-02 withdraw 42 "changed my mind"
                """);

        var CliExecutionResult = runner.runSuccessfully(List.of(path.toString()));

        assertEquals("""
                2022-02-02 open Assets:VanguardTaxable:Cash
                            
                2022-02-02 txn "deposit"
                  Equity:Bank
                  Assets:VanguardTaxable:Cash  42 GBP
                  
                2022-02-02 txn "withdraw - changed my mind"
                  Equity:Bank
                  Assets:VanguardTaxable:Cash  -42 GBP
                  
                """, CliExecutionResult.stdOut());
    }

    @Test
    void depositAndBuyCanBeCombined() {
        var path = TestFiles.writeToTempFile("""
                account Assets:VanguardTaxable
                currency GBP
                                
                2022-02-02 deposit and buy 2 VLS for 20
                """);

        var CliExecutionResult = runner.runSuccessfully(List.of(path.toString()));

        assertEquals("""
                2022-02-02 open Assets:VanguardTaxable:Cash
                            
                2022-02-02 txn "deposit"
                  Equity:Bank
                  Assets:VanguardTaxable:Cash  20 GBP

                2022-02-02 open Assets:VanguardTaxable:VLS
                            
                2022-02-02 txn "buy"
                  Assets:VanguardTaxable:Cash  -20 GBP
                  Assets:VanguardTaxable:VLS  2 VLS {10.0000000000 GBP} @@ 20 GBP
                  
                """, CliExecutionResult.stdOut());

    }

    @Test
    void depositAndBuyCanHaveAComment() {
        var path = TestFiles.writeToTempFile("""
                account Assets:VanguardTaxable
                currency GBP
                                
                2022-02-02 deposit and buy 2 VLS for 20 "payday investment"
                """);

        var CliExecutionResult = runner.runSuccessfully(List.of(path.toString()));

        assertEquals("""
                2022-02-02 open Assets:VanguardTaxable:Cash
                            
                2022-02-02 txn "deposit - payday investment"
                  Equity:Bank
                  Assets:VanguardTaxable:Cash  20 GBP

                2022-02-02 open Assets:VanguardTaxable:VLS
                            
                2022-02-02 txn "buy - payday investment"
                  Assets:VanguardTaxable:Cash  -20 GBP
                  Assets:VanguardTaxable:VLS  2 VLS {10.0000000000 GBP} @@ 20 GBP
                  
                """, CliExecutionResult.stdOut());
    }

    @Test
    void fee() {
        var path = TestFiles.writeToTempFile("""
                account Assets:A
                currency GBP
                                
                2022-02-02 deposit 2
                2022-02-02 fee 1
                """);

        var CliExecutionResult = runner.runSuccessfully(List.of(path.toString()));

        assertEquals("""
                2022-02-02 open Assets:A:Cash
                            
                2022-02-02 txn "deposit"
                  Equity:Bank
                  Assets:A:Cash  2 GBP

                2022-02-02 txn "fee"
                  Expenses:PlatformFee
                  Assets:A:Cash  -1 GBP
                  
                """, CliExecutionResult.stdOut());
    }

    @Test
    void interestInstruction() {
        var path = TestFiles.writeToTempFile("""
                account Assets:Savings
                currency GBP
                                
                2022-02-02 deposit 1
                2022-02-04 interest 0.1
                """);

        var CliExecutionResult = runner.runSuccessfully(List.of(path.toString()));

        assertEquals("""
                2022-02-02 open Assets:Savings:Cash
                            
                2022-02-02 txn "deposit"
                  Equity:Bank
                  Assets:Savings:Cash  1 GBP
                  
                2022-02-04 txn "interest"
                  Income:Interest
                  Assets:Savings:Cash  0.1 GBP

                """, CliExecutionResult.stdOut());
    }

    @Test
    void closingAccounts() {
        var path = TestFiles.writeToTempFile("""
                account Assets:Savings
                currency GBP
                                
                2022-02-02 deposit 1
                2022-02-02 withdraw 1
                2022-02-03 close
                """);

        var CliExecutionResult = runner.runSuccessfully(List.of(path.toString()));

        assertEquals("""
                2022-02-02 open Assets:Savings:Cash
                            
                2022-02-02 txn "deposit"
                  Equity:Bank
                  Assets:Savings:Cash  1 GBP
                  
                2022-02-02 txn "withdraw"
                  Equity:Bank
                  Assets:Savings:Cash  -1 GBP
                            
                2022-02-03 close Assets:Savings:Cash

                """, CliExecutionResult.stdOut());
    }

    @Test
    void feeInclusionInBuysAndSells() {
        var path = TestFiles.writeToTempFile("""
                account Assets:Shares
                currency GBP
                                
                2022-02-02 deposit 22
                2022-02-02 buy 1 X for 10 with fee 2
                2022-02-02 sell 1 X for 10 with fee 2
                """);

        var CliExecutionResult = runner.runSuccessfully(List.of(path.toString()));

        assertEquals("""
                2022-02-02 open Assets:Shares:Cash
                            
                2022-02-02 txn "deposit"
                  Equity:Bank
                  Assets:Shares:Cash  22 GBP

                2022-02-02 open Assets:Shares:X
                            
                2022-02-02 txn "buy"
                  Assets:Shares:Cash  -10 GBP
                  Assets:Shares:X  1 X {8.0000000000 GBP} @@ 8 GBP
                  Expenses:Commissions  2 GBP
                  
                2022-02-02 txn "sell"
                  Assets:Shares:Cash  10 GBP
                  Assets:Shares:X  -1 X {8.0000000000 GBP} @ 12.0000000000 GBP
                  Expenses:Commissions  2 GBP
                  Income:Gains:Shares
                  
                2022-02-02 close Assets:Shares:X
                  
                """, CliExecutionResult.stdOut());
    }

    @Test
    void supportsCurrenciesOtherThanGbp() {
        var path = TestFiles.writeToTempFile("""
                account Assets:Shares
                currency EUR
                                
                2022-02-02 deposit 22
                2022-02-02 buy 1 X for 10 with fee 2
                2022-02-02 sell 1 X for 10 with fee 2
                """);

        var CliExecutionResult = runner.runSuccessfully(List.of(path.toString()));

        assertEquals("""
                2022-02-02 open Assets:Shares:Cash
                            
                2022-02-02 txn "deposit"
                  Equity:Bank
                  Assets:Shares:Cash  22 EUR

                2022-02-02 open Assets:Shares:X
                            
                2022-02-02 txn "buy"
                  Assets:Shares:Cash  -10 EUR
                  Assets:Shares:X  1 X {8.0000000000 EUR} @@ 8 EUR
                  Expenses:Commissions  2 EUR
                  
                2022-02-02 txn "sell"
                  Assets:Shares:Cash  10 EUR
                  Assets:Shares:X  -1 X {8.0000000000 EUR} @ 12.0000000000 EUR
                  Expenses:Commissions  2 EUR
                  Income:Gains:Shares
                  
                2022-02-02 close Assets:Shares:X
                  
                """, CliExecutionResult.stdOut());
    }

    @Test
    void gainsAccountCanBeConfigured() {
        var path = TestFiles.writeToTempFile("""
                account Assets:Shares
                currency EUR
                                
                2022-02-02 deposit 22
                2022-02-02 buy 1 X for 10 with fee 2
                2022-02-02 sell 1 X for 10 with fee 2
                """);

        var CliExecutionResult = runner.runSuccessfully(List.of(path.toString(), "--gains-account", "Income:Gains:PortfolioA"));

        assertEquals("""
                2022-02-02 open Assets:Shares:Cash
                            
                2022-02-02 txn "deposit"
                  Equity:Bank
                  Assets:Shares:Cash  22 EUR

                2022-02-02 open Assets:Shares:X
                            
                2022-02-02 txn "buy"
                  Assets:Shares:Cash  -10 EUR
                  Assets:Shares:X  1 X {8.0000000000 EUR} @@ 8 EUR
                  Expenses:Commissions  2 EUR
                  
                2022-02-02 txn "sell"
                  Assets:Shares:Cash  10 EUR
                  Assets:Shares:X  -1 X {8.0000000000 EUR} @ 12.0000000000 EUR
                  Expenses:Commissions  2 EUR
                  Income:Gains:PortfolioA
                  
                2022-02-02 close Assets:Shares:X
                  
                """, CliExecutionResult.stdOut());
    }
}

