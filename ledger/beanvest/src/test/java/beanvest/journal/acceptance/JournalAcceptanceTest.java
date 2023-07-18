package beanvest.journal.acceptance;

import bb.lib.testing.AppRunner;
import bb.lib.testing.TestFiles;
import bb.lib.testing.AppRunnerFactory;
import beanvest.BeanvestMain;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class JournalAcceptanceTest {
    protected AppRunner runner = AppRunnerFactory.createRunner(BeanvestMain.class, "journal");

    @Test
    void printsBalanceAfterEachDay() {
        var path = TestFiles.writeToTempFile("""
                account Assets:VanguardTaxable
                currency GBP
                                
                2022-02-02 deposit 42
                2022-02-02 buy 2 VGB for 20
                2022-02-03 buy 2 VGB for 22
                2022-02-04 sell 3 VGB for 33
                2022-02-05 withdraw 33
                """);

        var result = runner.runSuccessfully(List.of(path.toString()));

        assertEquals("""
                        2022-02-02 deposit 42 GBP
                        2022-02-02 buy 2 VGB for 20 GBP
                          stats: dep: 42, wth: 0, int: 0, fee: 0, div: 0, rga: 0.00, csh: 22
                          holdings: n/a [2 VGB] (prices needed: VGB@2022-02-02)
                          
                        2022-02-03 buy 2 VGB for 22 GBP
                          stats: dep: 42, wth: 0, int: 0, fee: 0, div: 0, rga: 0.00, csh: 0
                          holdings: n/a [4 VGB] (prices needed: VGB@2022-02-03)
                          
                        2022-02-04 sell 3 VGB for 33 GBP
                          stats: dep: 42, wth: 0, int: 0, fee: 0, div: 0, rga: 1.50, csh: 33
                          holdings: n/a [1 VGB] (prices needed: VGB@2022-02-04)
                          
                        2022-02-05 withdraw 33 GBP
                          stats: dep: 42, wth: -33, int: 0, fee: 0, div: 0, rga: 1.50, csh: 0
                          holdings: n/a [1 VGB] (prices needed: VGB@2022-02-05)""",
                result.getStdOutWithoutLinesContaining("value:"));
    }

    @Test
    void itsNotPrintingPrices() {
        var path = TestFiles.writeToTempFile("""
                account A
                currency GBP
                                
                2022-01-01 deposit 5
                2022-02-02 price X 10 GBP
                2022-02-03 price Y 15 GBP
                """);

        var result = runner.runSuccessfully(List.of(path.toString()));

        assertEquals("2022-01-01 deposit 5 GBP\n" +
                "  stats: dep: 5, wth: 0, int: 0, fee: 0, div: 0, rga: 0.00, csh: 5\n" +
                "  holdings: 0.00 GBP []", result.stdOut().strip());
    }

    @Test
    void printsOnlyJournalOfSelectedAccount() {
        var journalA = TestFiles.writeToTempFile("""
                account A
                currency GBP
                                
                2022-02-02 deposit 10
                """);

        var journalB = TestFiles.writeToTempFile("""
                account B
                currency GBP
                                
                2022-02-02 deposit 20
                """);

        var result = runner.runSuccessfully(List.of(journalA.toString(), journalB.toString(), "--account=B"));

        assertEquals("""
                2022-02-02 deposit 20 GBP
                  stats: dep: 20, wth: 0, int: 0, fee: 0, div: 0, rga: 0.00, csh: 20
                  holdings: 0.00 GBP []
                  
                  """, result.stdOut());
    }

    @Test
    void printsOnlyInSelectedPeriod() {
        var journalA = TestFiles.writeToTempFile("""
                account A
                currency GBP
                                
                2022-02-02 deposit 10
                2022-02-03 deposit 10
                2022-02-04 deposit 10
                """);

        var result = runner.runSuccessfully(List.of(journalA.toString(), "--start=2022-02-03", "--end=2022-02-04"));

        assertEquals("""
                2022-02-03 deposit 10 GBP
                  stats: dep: 10, wth: 0, int: 0, fee: 0, div: 0, rga: 0.00, csh: 20
                  holdings: 0.00 GBP []
                  
                  """, result.stdOut());
    }

    @Test
    void printsCalculatedValues() {
        var journalA = TestFiles.writeToTempFile("""
                account A
                currency GBP
                                
                2022-02-02 deposit 10
                2022-02-02 price X 0.7 GBP
                2022-02-02 buy 10 X for 6
                """);

        var result = runner.runSuccessfully(List.of(journalA.toString()));

        assertEquals("""
                        2022-02-02 deposit 10 GBP
                        2022-02-02 buy 10 X for 6 GBP
                          holdings: 7.00 GBP [10 X]""",
                result.getStdOutWithoutLinesContaining("stats: "));
    }

    @Test
    void printsEverythingUpToTheProblematicDayIfValidationFails() {
        var journalA = TestFiles.writeToTempFile("""
                account A
                currency GBP
                                
                2022-02-02 deposit 10
                2022-02-03 deposit 20
                2022-02-04 balance 40
                """);

        var result = runner.run(List.of(journalA.toString()));

        assertEquals("""
                        2022-02-02 deposit 10 GBP
                          stats: dep: 10, wth: 0, int: 0, fee: 0, div: 0, rga: 0.00, csh: 10
                          holdings: 0.00 GBP []
                                               
                        2022-02-03 deposit 20 GBP
                          stats: dep: 30, wth: 0, int: 0, fee: 0, div: 0, rga: 0.00, csh: 30
                          holdings: 0.00 GBP []
                                               
                        2022-02-04 balance 40
                          stats: dep: 30, wth: 0, int: 0, fee: 0, div: 0, rga: 0.00, csh: 30
                          holdings: 0.00 GBP []
                                                    
                        """,
                result.stdOut());
        assertThat(result.stdErr()).matches("""
                (|.*)====> Ooops! Validation error:
                Cash balance does not match. Expected: 40. Actual: 30
                  @ /tmp/.*:6 2022-02-04 balance 40
                """);
    }

    @Test
    void exitCodeIsNotZeroIfValidationFails() {
        var journalA = TestFiles.writeToTempFile("""
                account A
                currency GBP
                                
                2022-02-02 deposit 10
                2022-02-03 deposit 20
                2022-02-04 balance 40 GBP
                """);

        var result = runner.run(List.of(journalA.toString()));

        assertNotEquals(0, result.exitCode());
    }

    @Test
    void printsComments() {
        var journalA = TestFiles.writeToTempFile("""
                account A
                currency GBP
                                
                2022-02-02 deposit 10 "commentA"
                2022-02-02 interest 1 "commentB"
                """);

        var result = runner.run(List.of(journalA.toString()));

        assertThat(result.stdOut())
                .contains("commentA")
                .contains("commentB");
    }
}

