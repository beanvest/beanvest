package beanvest.returns.acceptance.stats.delta;

import beanvest.returns.ReturnsDsl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class CashDeltaAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void calculatesCashYearlyDeltaSimpleCase() {
        dsl.setEnd("2023-01-01");
        dsl.setYearly();
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 100
                2022-01-02 deposit 101
                """);

        dsl.verifyCashDelta("trading", "2021", "100");
        dsl.verifyCashDelta("trading", "2022", "101");
    }

    @Test
    void calculatesCashYearlyDelta() {
        dsl.setEnd("2023-01-01");
        dsl.setYearly();
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 100
                2021-01-03 buy 1 X for 50
                2021-02-05 fee 1.2
                2021-12-31 price X 52.1

                                
                2022-02-04 sell 0.5 X for 28
                2022-02-05 fee 1.3
                                
                2022-12-31 price X 54.2
                """);

        dsl.verifyCashDelta("trading", "2021", "48.8");
        dsl.verifyCashDelta("trading", "2022", "26.7");
    }

}
