package beanvest.acceptance.returns;

import org.junit.jupiter.api.Test;

public class ProfitAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void shouldProfitFromUnrealizedGain() {
        dsl.setEnd("2022-01-01");
        dsl.setColumns("profit");

        dsl.runCalculateReturns("""
                account isa
                currency GBP
                                
                2021-01-01 deposit 10
                2021-01-01 buy 1 X for 5
                2021-12-31 price X 7 GBP
                """);

        dsl.verifyProfit("isa", "TOTAL", "2");
    }

    @Test
    void shouldProfitFromDividendsAndInterest() {
        dsl.setEnd("2022-01-01");
        dsl.setColumns("profit");

        dsl.runCalculateReturns("""
                account isa
                currency GBP
                                
                2021-01-01 deposit 10
                2021-01-01 buy 1 X for 10
                2021-12-31 dividend 2 from X
                2021-12-31 interest 3
                2021-12-31 price X 10
                """);

        dsl.verifyProfit("isa", "TOTAL", "5");
    }

    @Test
    void feesShouldReduceProfit() {
        dsl.setEnd("2022-01-01");
        dsl.setColumns("profit");

        dsl.runCalculateReturns("""
                account isa
                currency GBP
                                
                2021-01-01 deposit 10
                2021-01-01 buy 1 X for 10
                2021-12-31 sell 1 X for 20 with fee 1
                2021-12-31 fee 1
                """);

        dsl.verifyProfit("isa", "TOTAL", "8");
    }

    @Test
    void withdrawalsReduceProfitBasedAverageCost() {
        dsl.setEnd("2022-01-01");
        dsl.setColumns("profit");

        dsl.runCalculateReturns("""
                account isa
                currency GBP
                                
                2021-01-01 deposit 100
                2021-01-01 interest 10
                2021-01-01 withdraw 55
                """);
        /*
                                        value - cost = profit
                2021-01-01 deposit 100    100   100       0
                2021-01-01 interest 10    110   100      10
                2021-01-01 withdraw 55    100   110/2     5
         */

        dsl.verifyProfit("isa", "TOTAL", "5");
    }
}
