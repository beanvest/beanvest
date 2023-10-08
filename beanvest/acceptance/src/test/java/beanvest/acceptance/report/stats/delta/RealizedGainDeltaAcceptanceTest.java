package beanvest.acceptance.report.stats.delta;

import beanvest.acceptance.report.ReportDsl;
import org.junit.jupiter.api.Test;

public class RealizedGainDeltaAcceptanceTest {
    protected final ReportDsl dsl = new ReportDsl();

    @Test
    void realizedGainDeltasMayBeCalculatedPeriodically() {
        dsl.setColumns("RGain");
        dsl.setDeltas();
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
