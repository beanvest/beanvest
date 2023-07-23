package beanvest.acceptance.returns.stats.delta;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.Test;

public class DividendsDeltaAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void calculatesInterestYearlyDelta() {
        dsl.setEnd("2023-01-01");
        dsl.setYearly();
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 100
                2021-06-05 buy 1 X for 10
                2021-07-05 dividend 2 from X
                2021-12-31 price X 13.3
                                
                2022-07-05 dividend 1.9 from X
                2022-12-31 price X 13.2
                """);

        dsl.verifyDividendsDelta("trading", "2021", "2");
        dsl.verifyDividendsDelta("trading", "2022", "1.9");
    }
}
