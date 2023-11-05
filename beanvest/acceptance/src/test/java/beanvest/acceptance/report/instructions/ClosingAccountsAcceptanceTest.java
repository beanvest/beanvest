package beanvest.acceptance.report.instructions;

import beanvest.acceptance.report.dsl.ReportDsl;
import beanvest.lib.testing.DocumentsCurrentBehaviour;
import org.junit.jupiter.api.Test;

public class ClosingAccountsAcceptanceTest {
    protected final ReportDsl dsl = new ReportDsl();

    @Test
    void accountWithCashInItCantBeClosed() {
        dsl.setAllowNonZeroExitCodes();
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit 1
                2021-01-01 close
                """);

        dsl.verifyReturnedAnError("""
                ====> Ooops! Validation error:
                Account `trading` is not empty on 2021-01-01 and can't be closed. Holdings: 1 GBP.
                  @ /tmp/*.tmp:5 2021-01-01 close
                """);
        dsl.verifyDidNotPrintStackTrace();
    }

    @Test
    void accountWithHoldingsInItCantBeClosed() {
        dsl.setAllowNonZeroExitCodes();
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit and buy 1 VSTX for 10
                2021-01-01 close
                """);

        dsl.verifyReturnedAnError("""
                ====> Ooops! Validation error:
                Account `trading` is not empty on 2021-01-01 and can't be closed. Holdings: 1 VSTX.
                  @ /tmp/*.tmp:5 2021-01-01 close
                """);
        dsl.verifyDidNotPrintStackTrace();
    }

    @Test
    void closingBalanceIsCheckedForClosedAccountOnly() {
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit 1
                2021-01-02 deposit 1
                ---
                account trading2
                currency GBP
                                
                2021-01-02 deposit 2
                2021-01-03 withdraw 2
                2021-01-03 close
                ---
                """);
        dsl.verifyZeroExitCode();
    }

    @Test
    @DocumentsCurrentBehaviour(description = "consider doing the check at the end of the day")
    void orderOfCloseInstructionWithinTheDayIsImportant() {
        dsl.setAllowNonZeroExitCodes();

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit 2
                2021-01-02 close
                2021-01-02 withdraw 2
                ---
                """);
        dsl.verifyReturnedAnError("""
                ====> Ooops! Validation error:
                Account `trading` is not empty on 2021-01-02 and can't be closed. Holdings: 2 GBP.
                  @ /tmp/*.tmp:5 2021-01-02 close
                """);
        dsl.verifyNonZeroExitCode();
        dsl.verifyDidNotPrintStackTrace();
    }

    @Test
    void bug_bigDecimalNotEqualsZeroAfterSaleDueToDifferentScale() {
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 2
                2021-01-03 buy 1 X for 2
                2021-01-03 sell 0.5 X for 1.05
                2021-01-03 sell 0.5 X for 1.05
                2021-01-03 withdraw 2.1
                2021-01-03 close
                """);
        dsl.verifyZeroExitCode();
    }
}
