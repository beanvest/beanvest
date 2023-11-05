package beanvest.acceptance.importer;

import beanvest.BeanvestMain;
import beanvest.acceptance.report.dsl.BeanvestRunner;
import beanvest.lib.apprunner.AppRunner;
import beanvest.lib.testing.TestFiles;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ImporterAcceptanceTest {
    protected BeanvestRunner runner = BeanvestRunner.createRunner(BeanvestMain.class, "import");

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

    @Test
    void importIncomeTransactionsAsInterest() {
        var path = TestFiles.writeToTempFile("""
                2022-01-01 open Equity:Bank
                2022-01-01 open Income:Property:Rent
                                
                2022-02-12 * "rental income"
                  Income:Property:Rent    -2000 PLN
                  Equity:Bank
                """);

        var CliExecutionResult = runner.runSuccessfully(List.of(path.toString(), "Property", "NewProp"));

        assertEquals("""
                        account NewProp
                        currency PLN
                                        
                        2022-02-12 interest 2000 "rental income"
                        """,
                CliExecutionResult.stdOut());
    }

    @Test
    void importExpensesAsFees() {
        var path = TestFiles.writeToTempFile("""
                2022-01-01 open Equity:Bank
                2022-01-01 open Expenses:Property:Costs
                                
                2022-02-12 * "furniture"
                  Expenses:Property    2500 PLN
                  Equity:Bank
                """);

        var CliExecutionResult = runner.runSuccessfully(List.of(path.toString(), "Property", "NewProp"));

        assertEquals("""
                        account NewProp
                        currency PLN
                                        
                        2022-02-12 fee 2500 "furniture"
                        """,
                CliExecutionResult.stdOut());
    }

    @Test
    void printingDebugInfoInComment() {
        var path = TestFiles.writeToTempFile("""
                2022-01-01 open Equity:Bank
                2022-01-01 open Expenses:Property:Costs
                                
                2022-02-12 * "furniture"
                  Expenses:Property    2500 PLN
                  Equity:Bank
                """);

        var CliExecutionResult = runner.runSuccessfully(List.of(path.toString(), "Property", "NewProp", "--debug"));

        assertEquals("""
                        account NewProp
                        currency PLN
                                        
                        2022-02-12 fee 2500 "furniture @Expenses:Property"
                        """,
                CliExecutionResult.stdOut());
    }

    @Test
    void importTransactionsFromMultipleAccountsBasedOnPrefix() {
        var path = TestFiles.writeToTempFile("""
                2022-01-01 open Assets:Property
                2022-01-01 open Income:Property:TaxRefund
                2022-01-01 open Income:Property:Rent
                2022-01-01 open Equity:Bank
                                
                2022-02-10 * "Zakup"
                  Assets:Property    240000 PLN
                  Equity:Bank
                  
                2022-02-11 * "refund"
                  Income:Property:TaxRefund    -200 PLN
                  Equity:Bank
                  
                2022-02-12 * "rental income"
                  Income:Property:Rent    -2000 PLN
                  Equity:Bank
                """);

        var CliExecutionResult = runner.runSuccessfully(List.of(path.toString(), ".*Property.*", "NewProp"));

        assertEquals("""
                        account NewProp
                        currency PLN
                                        
                        2022-02-10 deposit 240000 "Zakup"
                        2022-02-11 interest 200 "refund"
                        2022-02-12 interest 2000 "rental income"
                        """,
                CliExecutionResult.stdOut());
    }

    @Test
    void specifiedAccountsCanBeIgnored() {
        var path = TestFiles.writeToTempFile("""
                2022-01-01 open Assets:Property
                2022-01-01 open Income:Property:TaxRefund
                2022-01-01 open Income:Property:Rent
                2022-01-01 open Equity:Bank
                                
                2022-02-10 * "Zakup"
                  Assets:Property    240000 PLN
                  Equity:Bank
                  
                2022-02-11 * "refund"
                  Income:Property:TaxRefund    -200 PLN
                  Equity:Bank
                  
                2022-02-12 * "rental income"
                  Income:Property:Rent    -2000 PLN
                  Equity:Bank
                """);

        var CliExecutionResult = runner.runSuccessfully(List.of(path.toString(), ".*Property.*", "NewProp", "--ignore=Income:Property:TaxRefund"));

        assertEquals("""
                        account NewProp
                        currency PLN
                                        
                        2022-02-10 deposit 240000 "Zakup"
                        2022-02-12 interest 2000 "rental income"
                        """,
                CliExecutionResult.stdOut());
    }


    @Test
    void shouldImportWithNoCashStoredFromInterestAndFees() {
        var path = TestFiles.writeToTempFile("""
                2022-01-01 open Assets:Property
                2022-01-01 open Income:Property:Rent
                2022-01-01 open Equity:Bank
                2022-01-01 open Expenses:Property:Rent
                                
                2022-02-10 * "Zakup"
                  Assets:Property    240000 PLN
                  Equity:Bank
                  
                2022-02-12 * "rental income"
                  Income:Property:Rent
                  Equity:Bank           2000 PLN
                  
                2022-02-13 * "rental costs"
                  Expenses:Property:Rent
                  Equity:Bank             -1000 PLN
                """);

        var CliExecutionResult = runner.runSuccessfully(List.of(path.toString(), ".*Property.*", "NewProp", "--no-cash"));

        assertEquals("""
                        account NewProp
                        currency PLN
                                        
                        2022-02-10 deposit 240000 "Zakup"
                        2022-02-12 interest 2000 "rental income"
                        2022-02-12 withdraw 2000 "rental income"
                        2022-02-13 deposit 1000 "rental costs"
                        2022-02-13 fee 1000 "rental costs"
                        """,
                CliExecutionResult.stdOut());
    }

    @Test
    void shouldImportWithNoCashStoredFromInterestAndFeesButMoveMightBeInvertedIfAmountIsNegative() {
        var path = TestFiles.writeToTempFile("""
                2022-01-01 open Assets:Property
                2022-01-01 open Income:Property:Rent
                2022-01-01 open Equity:Bank
                2022-01-01 open Expenses:Property:Cost
                                
                2022-02-10 * "Zakup"
                  Assets:Property    240000 PLN
                  Equity:Bank
                  
                2022-02-12 * "rental income overpayment returned"
                  Income:Property:Rent
                  Equity:Bank            -20 PLN
                  
                2022-02-13 * "rental costs overpayment returned"
                  Expenses:Property:Cost
                  Equity:Bank             10 PLN
                """);

        var CliExecutionResult = runner.runSuccessfully(List.of(path.toString(), ".*Property.*", "NewProp", "--no-cash"));

        assertEquals("""
                        account NewProp
                        currency PLN
                                        
                        2022-02-10 deposit 240000 "Zakup"
                        2022-02-12 deposit 20 "rental income overpayment returned"
                        2022-02-12 interest -20 "rental income overpayment returned"
                        2022-02-13 fee -10 "rental costs overpayment returned"
                        2022-02-13 withdraw 10 "rental costs overpayment returned"
                        """,
                CliExecutionResult.stdOut());
    }
}

