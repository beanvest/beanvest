package beanvest.acceptance.returns.instructions;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ClosingAccountsAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    @Disabled("validator needs reimplementing")
    void notEmptyAccountCannotBeClosed() {
        dsl.setAllowNonZeroExitCodes();
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit 1
                2021-01-01 close
                """);

        dsl.verifyReturnedAnError("""
                ====> Ooops! Validation error:
                Account `trading` is not empty on 2021-01-01 and can't be closed. Inventory: [] and 1 GBP cash
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
}
