package beanvest.returns.acceptance.stats.delta;

import beanvest.returns.ReturnsDsl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class InterestDeltaAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void calculatesInterestYearlyDelta() {
        dsl.setEnd("2023-01-01");
        dsl.setYearly();
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 100
                2021-06-05 interest 5
                2021-07-05 interest 2
                                
                2022-06-05 interest 10
                2022-07-12 interest 12.31
                2022-07-13 interest -1
                """);

        dsl.verifyInterestDelta("trading", "2021", "7");
        dsl.verifyInterestDelta("trading", "2022", "21.31");
    }
}
