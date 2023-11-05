package beanvest.acceptance.report;

import beanvest.acceptance.report.dsl.ReportDsl;
import org.junit.jupiter.api.Test;

public class DecimalPrecisionAcceptanceTest {
    protected ReportDsl dsl = new ReportDsl();

    @Test
    void shouldHandleAnyDecimalPrecision() {
        dsl.setYearly();
        dsl.setReportHoldings();

        dsl.calculateReturns("""
                account pension
                currency GBP
                                
                2022-01-01 deposit and buy  0.04313771 ETH for 100
                2022-01-02 sell             0.04313771 ETH for 108
                2022-01-03 balance 108
                2022-01-04 withdraw 108
                2022-01-05 balance 0 ETH
                2022-01-06 close
                
                """);

        dsl.verifyNoWarningsShown();

    }
}
