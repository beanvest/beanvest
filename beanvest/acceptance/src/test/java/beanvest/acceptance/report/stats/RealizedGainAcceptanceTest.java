package beanvest.acceptance.report.stats;

import beanvest.acceptance.report.dsl.ReportDsl;
import org.junit.jupiter.api.Test;

public class RealizedGainAcceptanceTest {
    protected final ReportDsl dsl = new ReportDsl();

    @Test
    void realizedGainIsBasedOnAverageCost() {
        dsl.setColumns("RGain");
        dsl.setEnd("2021-01-03");
        dsl.setReportHoldings();
        dsl.calculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 10
                2021-01-03 buy 1 X for 10
                2021-01-03 buy 1 X for 20
                2021-01-03 sell 1 X for 16
                """);

        dsl.verifyRealizedGains("trading", "TOTAL", "1");
        dsl.verifyRealizedGains("trading:X", "TOTAL", "1");
    }

    @Test
    void realizedGainsAreReducedBySellingFees() {
        dsl.setColumns("RGain");
        dsl.calculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 10
                2021-01-03 buy 1 X for 10
                2021-01-03 sell 1 X for 12 with fee 1
                """);
        dsl.verifyRealizedGains("trading", "TOTAL", "1");
    }

    @Test
    void realizedGainsAreReducedByBuyingFees() {
        dsl.setColumns("RGain");
        dsl.setEnd("2021-01-03");
        dsl.calculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 10
                2021-01-03 buy 1 X for 10
                2021-01-03 buy 1 X for 11 with fee 0.2
                2021-01-03 sell 1 X for 12
                2021-01-03 price X 100
                """);
        dsl.verifyRealizedGains("trading", "TOTAL", "1.50");
    }

    @Test
    void calculatesGainWhenSellingInParts() {
        dsl.setColumns("RGain");
        dsl.setEnd("2021-01-03");
        dsl.calculateReturns("""
                account trading
                currency GBP
                              
                2016-03-28 deposit and buy 2 FIDUS for 100
                2016-09-10 sell 1 FIDUS for 60
                2016-09-10 sell 1 FIDUS for 60
                """);
        dsl.verifyRealizedGains("trading", "TOTAL", "20");
    }

    @Test
    void realizedGainMayBeCalculatedPeriodicallyCumulatively() {
        dsl.setColumns("RGain");
        dsl.setEnd("2023-01-01");
        dsl.setYearly();
        dsl.calculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 10
                2021-01-03 buy 1 X for 10
                2021-01-04 sell 1 X for 11
                2022-01-03 buy 1 X for 10
                2022-01-04 sell 1 X for 12
                """);

        dsl.verifyRealizedGains("trading", "2021", "1");
        dsl.verifyRealizedGains("trading", "2022", "3");
    }

    @Test
    void shouldCalculateRealizedGainOfHoldings() {
        dsl.setColumns("RGain");
        dsl.setReportHoldings();
        dsl.setEnd("2023-01-01");
        dsl.setYearly();
        dsl.calculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 10
                2021-01-03 buy 1 X for 10
                2021-01-04 sell 1 X for 11
                2022-01-03 buy 1 X for 10
                2022-01-04 sell 1 X for 12
                """);

        dsl.verifyRealizedGains("trading:X", "2021", "1");
        dsl.verifyRealizedGains("trading:X", "2022", "3");
    }
}
