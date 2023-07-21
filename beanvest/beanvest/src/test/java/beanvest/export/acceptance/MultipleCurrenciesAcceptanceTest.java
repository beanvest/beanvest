package beanvest.export.acceptance;

import bb.lib.testing.AppRunner;
import bb.lib.testing.TestFiles;
import bb.lib.testing.AppRunnerFactory;
import beanvest.BeanvestMain;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MultipleCurrenciesAcceptanceTest {
    protected AppRunner runner = AppRunnerFactory.createRunner(BeanvestMain.class, "export");

    @Test
    void otherCurrencyInTransactions() {
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
    void otherCurrencyInWithdrawals() {
        var path = TestFiles.writeToTempFile("""
                account savings
                currency EUR
                                
                2022-02-02 deposit 20
                2022-02-03 withdraw 20
                """);

        var CliExecutionResult = runner.runSuccessfully(List.of(path.toString()));

        assertEquals("""
                2022-02-02 open savings:Cash
                                                 
                2022-02-02 txn "deposit"
                  Equity:Bank
                  savings:Cash  20 EUR
                                 
                2022-02-03 txn "withdraw"
                  Equity:Bank
                  savings:Cash  -20 EUR
                                 
                """, CliExecutionResult.stdOut());
    }

    @Test
    void otherCurrencyInFees() {
        var path = TestFiles.writeToTempFile("""
                account savings
                currency EUR
                                
                2022-02-02 deposit 20
                2022-02-03 fee 2
                """);

        var CliExecutionResult = runner.runSuccessfully(List.of(path.toString()));

        assertEquals("""
                2022-02-02 open savings:Cash
                                
                2022-02-02 txn "deposit"
                  Equity:Bank
                  savings:Cash  20 EUR
                                
                2022-02-03 txn "fee"
                  Expenses:PlatformFee
                  savings:Cash  -2 EUR
                  
                  """, CliExecutionResult.stdOut());
    }

    @Test
    void otherCurrencyInInterest() {
        var path = TestFiles.writeToTempFile("""
                account savings
                currency EUR
                                
                2022-02-02 deposit 20
                2022-02-03 interest 2
                """);

        var CliExecutionResult = runner.runSuccessfully(List.of(path.toString()));

        assertEquals("""
                2022-02-02 open savings:Cash
                                
                2022-02-02 txn "deposit"
                  Equity:Bank
                  savings:Cash  20 EUR
                                
                2022-02-03 txn "interest"
                  Income:Interest
                  savings:Cash  2 EUR
                  
                  """, CliExecutionResult.stdOut());
    }
}
