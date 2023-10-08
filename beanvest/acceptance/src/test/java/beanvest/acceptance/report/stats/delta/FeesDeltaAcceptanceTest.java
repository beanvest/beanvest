package beanvest.acceptance.report.stats.delta;

import beanvest.acceptance.report.ReportDsl;
import org.junit.jupiter.api.Test;
public class FeesDeltaAcceptanceTest {
    protected final ReportDsl dsl = new ReportDsl();

    @Test
    void calculatesFeesYearlyDelta() {
        dsl.setColumns("Fees");
        dsl.setDeltas();
        dsl.setEnd("2023-01-01");
        dsl.setYearly();
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 100
                2021-01-03 buy 1 X for 50 with fee 1
                2021-02-04 sell 1 X for 50 with fee 2
                2021-02-05 fee 1.2
                                
                2022-01-03 buy 1 X for 50 with fee 1
                2022-02-04 sell 1 X for 50 with fee 1.1
                2022-02-05 fee 1.2
                """);

        dsl.verifyFeesDelta("trading", "2021", "-4.2");
        dsl.verifyFeesDelta("trading", "2022", "-3.3");
    }
}
