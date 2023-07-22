package beanvest.acceptance.returns.instructions;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("processing refactor")
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
    void validationMightFailOnMultipleBalanceEntries() {
        dsl.setAllowNonZeroExitCodes();
        dsl.setEnd("2021-01-02");
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit and buy 1 X for 10
                2021-01-01 balance 5
                2021-01-01 balance 2 X
                2021-01-01 price X 10
                """);

        dsl.verifyNonZeroExitCode();
        dsl.verifyReturnedAnError("""
                ====> Ooops! Validation errors:
                Cash balance does not match. Expected: 5. Actual: 0
                  @ /tmp/*.tmp:5 2021-01-01 balance 5
                Holding balance does not match. Expected: 2 X. Actual: 1 X
                  @ /tmp/*.tmp:6 2021-01-01 balance 2 X
                """);
    }
}
