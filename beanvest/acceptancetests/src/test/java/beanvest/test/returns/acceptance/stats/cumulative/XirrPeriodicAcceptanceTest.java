package beanvest.test.returns.acceptance.stats.cumulative;

import beanvest.test.returns.ReturnsDsl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class XirrPeriodicAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();
    @Test
    void calculatesXirrForEachPeriodSeparately() {
        dsl.setStartDate("2020-01-01");
        dsl.setEnd("2022-01-01");
        dsl.setYearly();

        dsl.runCalculateReturns("""
                account savings
                currency GBP
                            
                2020-01-01 deposit 100
                2020-12-31 interest 100
                2021-12-31 interest 100
                """);

        dsl.verifyXirrPeriodic("savings", "2020", "100");
        dsl.verifyXirrPeriodic("savings", "2021", "50");
    }
}
