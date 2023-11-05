package beanvest.acceptance.report.stats.delta;

import beanvest.acceptance.report.dsl.ReportDsl;
import org.junit.jupiter.api.Test;
public class OpenAndCloseDatesDeltaAcceptanceTest {
    protected final ReportDsl dsl = new ReportDsl();

    @Test
    void accountReturnsMightBeEmptyIfAccountWasClosedInAPeriod() {
        dsl.setYearly();
        dsl.setStartDate("2021-01-01");
        dsl.setEnd("2023-01-01");
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit and buy 10 A for 10
                2021-12-31 price A 1.14 GBP
                2022-12-31 price A 1.00 GBP
                ---
                account pension
                currency GBP
                                
                2022-01-02 deposit and buy 10 X for 10
                2022-12-31 price X 1.1 GBP
                """);

        dsl.verifyHasStats("trading", "2021");
        dsl.verifyHasStats("trading", "2022");
        dsl.verifyHasNoStats("pension", "2021");
        dsl.verifyHasStats("pension", "2022");
    }
}
