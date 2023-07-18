package beanvest.returns.acceptance;

import beanvest.returns.ReturnsDsl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class AccountGainAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void considerSellsWithWithdrawalsWhenCalculatingGain() {
        dsl.setEnd("2022-01-01");

        dsl.runCalculateReturns("""
                account isa
                currency GBP
                                
                2021-01-01 deposit 100
                2021-01-01 buy 5 APPL for 100
                2021-12-31 sell and withdraw 1 APPL for 22
                2022-01-01 price APPL 22 GBP
                """);

        dsl.verifyAccountGain("isa", "TOTAL", "10");
    }

    @Test
    void considerDepositsWhenCalculatingGain() {
        dsl.setEnd("2022-01-01");

        dsl.runCalculateReturns("""
                account isa
                commodity X
                currency GBP
                                
                2021-01-01 deposit 10
                2021-01-01 buy 1 X for 5
                2022-01-01 price X 10 GBP
                """);

        dsl.verifyAccountGain("isa", "TOTAL", "5");
    }

    @Test
    void considerDepositsForBuysWhenCalculatingGain() {
        dsl.setEnd("2022-01-01");

        dsl.runCalculateReturns("""
                account isa
                commodity VLS
                currency GBP
                               
                2021-01-01 deposit and buy 1 VLS for 100
                2022-01-01 price VLS 110 GBP
                """);

        dsl.verifyAccountGain("isa", "TOTAL", "10");
    }

    // ending cash is used in the formula but not starting cash
    // while we also have delta of value
}
