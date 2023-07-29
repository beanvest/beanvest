package beanvest.acceptance.returns.instructions;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class BalanceAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void nothingHappensIfBalanceMatches() {
        dsl.runCalculateReturns("""
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
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit 1
                2021-01-01 balance 2 GBP
                """);

        dsl.verifyNonZeroExitCode();
        dsl.verifyReturnedAnError("""
                ====> Ooops! Validation error:
                Cash balance does not match. Expected: 2. Actual: 1
                  @ /tmp/*.tmp:5 2021-01-01 balance 2 GBP
                """);
    }

    @Test
    void validationFailsIfHoldingBalanceDoesNotMatch() {
        dsl.setAllowNonZeroExitCodes();
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit and buy 1 X for 10
                2021-01-01 balance 1.1 X
                """);

        dsl.verifyNonZeroExitCode();
        dsl.verifyReturnedAnError("""
                ====> Ooops! Validation error:
                Holding balance does not match. Expected: 1.1 X. Actual: 1 X
                  @ /tmp/*.tmp:5 2021-01-01 balance 1.1 X
                """);
    }

    @Test
    void balanceIsCheckedForJustOneAccount() {
        dsl.setAllowNonZeroExitCodes();
        dsl.runCalculateReturns("""
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
}
