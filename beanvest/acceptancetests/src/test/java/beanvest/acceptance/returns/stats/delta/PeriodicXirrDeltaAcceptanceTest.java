package beanvest.acceptance.returns.stats.delta;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("implement as periodic xirr")
public class PeriodicXirrDeltaAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void calculatesXirrForEachPeriodIfPeriodicDeltasRequested() {
        dsl.setYearly();
        dsl.setEnd("2023-01-01");
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit and buy 10 VLS for 10
                2021-12-31 price VLS 1.14 GBP
                2022-12-31 price VLS 1.00 GBP
                """);

//        dsl.verifyPeriodicXirrDelta("trading", "2021", "14");  +14
//        dsl.verifyPeriodicXirrDelta("trading", "2022", "-26.3"); from 14 to -12.3 so: (-14 + -12.3) = -26.3
    }
}
