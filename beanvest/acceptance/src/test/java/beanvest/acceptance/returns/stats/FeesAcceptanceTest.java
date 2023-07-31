package beanvest.acceptance.returns.stats;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.Test;

public class FeesAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void calculatesFeesTotal() {
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

        dsl.verifyFeesTotal("trading", "TOTAL", "-7.5");
    }

    @Test
    void feeMightBeNegativeInCaseOfReturnedFee() {
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 100
                2021-02-05 fee 1.2
                2021-02-06 fee -1
                """);

        dsl.verifyFeesTotal("trading", "TOTAL", "-0.2");
    }

    @Test
    void shouldReturnSumOfCommissionsForHolding() {
        dsl.setReportHoldings();

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 100
                2021-02-05 fee 1.2
                2021-02-06 buy 1 APPL for 90 with fee 2
                """);

        dsl.verifyFeesTotal("trading:APPL", "TOTAL", "-2");
    }
}
