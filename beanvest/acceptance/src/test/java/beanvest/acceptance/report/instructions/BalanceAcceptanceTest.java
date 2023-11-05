package beanvest.acceptance.report.instructions;

import beanvest.acceptance.report.dsl.ReportDsl;
import org.junit.jupiter.api.Test;

public class BalanceAcceptanceTest {
    protected final ReportDsl dsl = new ReportDsl();

    @Test
    void nothingHappensIfBalanceMatches() {
        dsl.calculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit 1
                2021-01-01 balance 1 GBP
                """);
        dsl.verifyZeroExitCode();
    }

    @Test
    void validationFailsIfCashBalanceDoesNotMatch() {
        dsl.setAllowNonZeroExitCodes();
        dsl.calculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit 1
                2021-01-01 balance 2 GBP
                """);

        dsl.verifyNonZeroExitCode();
        dsl.verifyReturnedAnError("""
                ====> Ooops! Validation error:
                Balance does not match. Expected: 2 GBP. Actual: 1 GBP
                  @ /tmp/*.tmp:5 2021-01-01 balance 2 GBP
                """);
    }

    @Test
    void validationFailsIfHoldingBalanceDoesNotMatch() {
        dsl.setAllowNonZeroExitCodes();
        dsl.calculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit and buy 1 X for 10
                2021-01-01 balance 1.1 X
                """);

        dsl.verifyNonZeroExitCode();
        dsl.verifyReturnedAnError("""
                ====> Ooops! Validation error:
                Balance does not match. Expected: 1.1 X. Actual: 1 X
                  @ /tmp/*.tmp:5 2021-01-01 balance 1.1 X
                """);
    }

    @Test
    void balanceIsCheckedForJustOneAccount() {
        dsl.setAllowNonZeroExitCodes();
        dsl.calculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit and buy 5 X for 50
                ---
                account trading2
                currency GBP
                                
                2021-01-01 deposit and buy 1 X for 10
                2021-01-01 balance 1 X
                """);

        dsl.verifyZeroExitCode();
    }

    @Test
    void orderOfBalanceInstructionWithinTheDayIsImportant() {
        dsl.setAllowNonZeroExitCodes();

        dsl.calculateReturns("""
                account trading2
                currency GBP
                                
                2021-01-01 balance 1
                2021-01-01 deposit 1
                ---
                """);
        dsl.verifyReturnedAnError("""
                ====> Ooops! Validation error:
                Balance does not match. Expected: 1 GBP. Actual: 0 GBP
                  @ /tmp/*.tmp:4 2021-01-01 balance 1
                """);
        dsl.verifyNonZeroExitCode();
        dsl.verifyDidNotPrintStackTrace();
    }
}
