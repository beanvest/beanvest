package beanvest.importer.acceptance;

import bb.lib.testing.AppRunner;
import bb.lib.testing.AppRunnerFactory;
import bb.lib.testing.TestFiles;
import beanvest.BeanvestMain;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled("needs fixing")
public class ImporterAcceptanceTest {
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
}

