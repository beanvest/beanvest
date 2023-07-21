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
public class DuplicatedTransactionsAcceptanceTest {
    protected AppRunner runner = AppRunnerFactory.createRunner(BeanvestMain.class, "import");
    @Test
    void preferIncomeExpenseIfDuplicatedTransaction() {
        var path = TestFiles.writeToTempFile("""
                2022-01-01 open Assets:Property
                2022-01-01 open Income:Property:TaxRefund
                2022-01-01 open Income:Property:Rent
                2022-01-01 open Equity:Bank
                                
                2022-02-10 * "Zakup"
                  Assets:Property    240000 PLN
                  Equity:Bank
                  
                2022-02-12 * "rental income"
                  Income:Property:Rent    -200 PLN
                  Assets:Property

                """);
        var CliExecutionResult = runner.runSuccessfully(List.of(path.toString(), ".*Property.*", "NewProp"));

        assertEquals("""
                        account NewProp
                        currency PLN
                                        
                        2022-02-10 deposit 240000 "Zakup"
                        2022-02-12 interest 200 "rental income"
                        """,
                CliExecutionResult.stdOut());
    }

    @Test
    void duplicatedIsOkayIfTypeIsTheSameAsTheyAreJustSplits() {
        var path = TestFiles.writeToTempFile("""
                2022-01-01 open Assets:Property
                2022-01-01 open Income:Property:TaxRefund
                2022-01-01 open Income:Property:Rent
                2022-01-01 open Equity:Bank
                  
                2022-02-12 * "rental income"
                  Income:Property:Rent         -200 PLN
                  Income:Property:TaxRefund    -100 PLN
                  Assets:Property

                """);
        var CliExecutionResult = runner.runSuccessfully(List.of(path.toString(), ".*Property.*", "NewProp"));

        assertEquals("""
                        account NewProp
                        currency PLN
                                        
                        2022-02-12 interest 300 "rental income"
                        """,
                CliExecutionResult.stdOut());
    }
}

