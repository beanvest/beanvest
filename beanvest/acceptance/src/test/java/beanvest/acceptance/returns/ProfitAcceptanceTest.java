package beanvest.acceptance.returns;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("Work in progress")
public class ProfitAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void considerDepositsWhenCalculatingProfit() {
        dsl.setEnd("2022-01-01");
        dsl.setColumns("profit,ncost");

        dsl.runCalculateReturns("""
                account isa
                currency GBP
                                
                2021-01-01 deposit 10
                2021-01-01 buy 1 X for 5
                2021-12-31 price X 5 GBP
                """);

        dsl.verifyCost("isa", "TOTAL", "10");
        dsl.verifyProfit("isa", "TOTAL", "0");
    }

    @Test
    void considerDepositsForBuysWhenCalculatingGain() {
        dsl.setEnd("2022-01-01");

        dsl.runCalculateReturns("""
                account isa
                currency GBP
                               
                2021-01-01 deposit and buy 1 VLS for 100
                2022-01-01 price VLS 110 GBP
                """);

        dsl.verifyProfit("isa", "TOTAL", "10");
    }

    // ending cash is used in the formula but not starting cash
    // while we also have delta of value
    @Test
    void considerSellsWithWithdrawalsWhenCalculatingGain() {
        dsl.setEnd("2022-01-01");
        dsl.setColumns("profit");
        dsl.runCalculateReturns("""
                account isa
                currency GBP
                                
                2021-01-01 deposit 100
                2021-01-01 buy 5 APPL for 100
                2021-12-31 sell and withdraw 1 APPL for 22
                2022-01-01 price APPL 22 GBP
                """);

        dsl.verifyProfit("isa", "TOTAL", "10");
    }
}
