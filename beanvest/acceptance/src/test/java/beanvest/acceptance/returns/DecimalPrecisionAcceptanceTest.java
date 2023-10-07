package beanvest.acceptance.returns;

import org.junit.jupiter.api.Test;

public class DecimalPrecisionAcceptanceTest {
    protected ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void shouldHandleAnyDecimalPrecision() {
        dsl.setYearly();
        dsl.setReportHoldings();

        dsl.runCalculateReturns("""
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