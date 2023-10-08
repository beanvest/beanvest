package beanvest.acceptance.report.stats.delta;

import beanvest.acceptance.report.ReportDsl;
import org.junit.jupiter.api.Test;

public class UnrealizedGainDeltaAcceptanceTest {
    protected final ReportDsl dsl = new ReportDsl();

    @Test
    void calculatesUnrealizedGainDeltasYearly() {
        dsl.setEnd("2023-01-01");
        dsl.setYearly();
        dsl.setColumns("UGain");
        dsl.setDeltas();
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 10
                2021-01-03 buy 1 X for 10
                                
                2021-12-31 price X 11
                2022-12-31 price X 14
                """);

        dsl.verifyUnrealizedGainsDelta("trading", "2021", "1");
        dsl.verifyUnrealizedGainsDelta("trading", "2022", "3");
    }
}
