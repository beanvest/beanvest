package beanvest.test.returns.acceptance.stats.delta;

import beanvest.test.returns.ReturnsDsl;
import org.junit.jupiter.api.Test;

public class RealizedGainDeltaAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void realizedGainDeltasMayBeCalculatedPeriodically() {
        dsl.setEnd("2023-01-01");
        dsl.setYearly();
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 10
                2021-01-03 buy 1 X for 10
                2021-01-04 sell 1 X for 11
                2022-01-03 buy 1 X for 10
                2022-01-04 sell 1 X for 12
                """);

        dsl.verifyRealizedGainsDelta("trading", "2021", "1");
        dsl.verifyRealizedGainsDelta("trading", "2022", "2");
    }
}
