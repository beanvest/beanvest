package beanvest.acceptance.export;

import beanvest.BeanvestMain;
import beanvest.lib.apprunner.AppRunner;
import beanvest.lib.apprunner.AppRunnerFactory;
import beanvest.lib.testing.TestFiles;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled("broken by rework v2, needs reevaluation")
public class UnrealizedGainsAcceptanceTest {
    protected AppRunner runner = AppRunnerFactory.createRunner(BeanvestMain.class, "export");

    @Test
    void printsChangingUnrealizedGainsAsTransactions() {
        var path = TestFiles.writeToTempFile("""
                account Assets:Shares
                currency EUR
                                
                2022-02-02 deposit 10
                2022-02-02 buy 1 X for 10
                2022-02-03 price X 11
                2022-02-04 price X 12
                """);

        var CliExecutionResult = runner.runSuccessfully(List.of(path.toString(), "--stats=UG"));

        assertEquals("""
                2022-02-03 open Assets:Shares:Stats
                            
                2022-02-03 txn "interest - autogains"
                  Income:Stats
                  Assets:Shares:Stats  1 UGXEUR
                  
                2022-02-04 txn "interest - autogains"
                  Income:Stats
                  Assets:Shares:Stats  1 UGXEUR
                  
                """, CliExecutionResult.stdOut());
    }

    @Test
    void printsChangingAccountGainsAsTransactions() {
        var path = TestFiles.writeToTempFile("""
                account Assets:Shares
                currency EUR
                                
                2022-02-02 deposit 10
                2022-02-02 buy 1 X for 9
                2022-02-03 price X 10
                2022-02-03 withdraw 1
                2022-02-04 interest 1
                2022-02-05 dividend 1 from X
                """);

        var CliExecutionResult = runner.runSuccessfully(List.of(path.toString(), "--stats=AG"));

        assertEquals("""
                2022-02-03 open Assets:Shares:Stats
                            
                2022-02-03 txn "interest - autogains"
                  Income:Stats
                  Assets:Shares:Stats  1 AGXEUR
                  
                2022-02-04 txn "interest - autogains"
                  Income:Stats
                  Assets:Shares:Stats  1 AGXEUR
                  
                2022-02-05 txn "interest - autogains"
                  Income:Stats
                  Assets:Shares:Stats  1 AGXEUR
                  
                """, CliExecutionResult.stdOut());
    }

    @Test
    void uses4decimalDigitsTops() {
        var path = TestFiles.writeToTempFile("""
                account Assets:Shares
                currency EUR
                                
                2022-02-02 deposit 10
                2022-02-02 buy 1 X for 10
                2022-02-03 price X 0.0000000000000000000000001
                """);

        var CliExecutionResult = runner.runSuccessfully(List.of(path.toString(), "--stats=UG"));

        assertEquals("""
                2022-02-03 open Assets:Shares:Stats
                            
                2022-02-03 txn "interest - autogains"
                  Income:Stats
                  Assets:Shares:Stats  -10.0000 UGXEUR
                  
                """, CliExecutionResult.stdOut());
    }

    @Test
    void orderOfGeneratedStatsIsDeterministic() {
        var path = TestFiles.writeToTempFile("""
                account Assets:Shares
                currency EUR
                                
                2022-02-02 deposit 10
                2022-02-02 buy 1 X for 10
                2022-02-03 price X 11
                2022-02-04 price X 10
                2022-02-05 price X 11
                2022-02-06 price X 10
                2022-02-07 price X 11
                                
                """);

        var cliExecutionResult = runner.runSuccessfully(List.of(path.toString(), "--stats=UG,AG"));

        assertEquals("""
                2022-02-03 open Assets:Shares:Stats
                                
                2022-02-03 txn "interest - autogains"
                  Income:Stats
                  Assets:Shares:Stats  1 AGXEUR
                                
                2022-02-03 txn "interest - autogains"
                  Income:Stats
                  Assets:Shares:Stats  1 UGXEUR
                                
                2022-02-04 txn "interest - autogains"
                  Income:Stats
                  Assets:Shares:Stats  -1 AGXEUR
                                
                2022-02-04 txn "interest - autogains"
                  Income:Stats
                  Assets:Shares:Stats  -1 UGXEUR
                                
                2022-02-05 txn "interest - autogains"
                  Income:Stats
                  Assets:Shares:Stats  1 AGXEUR
                                
                2022-02-05 txn "interest - autogains"
                  Income:Stats
                  Assets:Shares:Stats  1 UGXEUR
                                
                2022-02-06 txn "interest - autogains"
                  Income:Stats
                  Assets:Shares:Stats  -1 AGXEUR
                                
                2022-02-06 txn "interest - autogains"
                  Income:Stats
                  Assets:Shares:Stats  -1 UGXEUR
                                
                2022-02-07 txn "interest - autogains"
                  Income:Stats
                  Assets:Shares:Stats  1 AGXEUR
                                
                2022-02-07 txn "interest - autogains"
                  Income:Stats
                  Assets:Shares:Stats  1 UGXEUR
                                               
                """, cliExecutionResult.stdOut());
    }
}
