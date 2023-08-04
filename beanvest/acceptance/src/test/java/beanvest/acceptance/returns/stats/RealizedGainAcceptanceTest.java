package beanvest.acceptance.returns.stats;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("rework v2")
public class RealizedGainAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void realizedGainIsBasedOnAveragePurchasePrice() {
        dsl.setEnd("2021-01-03");
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 10
                2021-01-03 buy 1 X for 10 with fee 1
                2021-01-03 sell 1 X for 10 with fee 1
                """);

        dsl.verifyRealizedGains("trading", "TOTAL", "0");
    }

    @Test
    void realizedGainsAreReducedBySellingFees() {
        dsl.setEnd("2021-01-03");
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 10
                2021-01-03 buy 1 X for 10
                2021-01-03 sell 1 X for 10 with fee 1
                2021-01-03 price X 10
                """);
        dsl.verifyRealizedGains("trading", "TOTAL", "0");
    }

    @Test
    void realizedGainsAreReducedByBuyingFees() {
        dsl.setEnd("2021-01-03");
        dsl.runCalculateReturns("""
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
        dsl.setEnd("2021-01-03");
        dsl.runCalculateReturns("""
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

        dsl.verifyRealizedGains("trading", "2021", "1");
        dsl.verifyRealizedGains("trading", "2022", "3");
    }

    @Test
    void shouldCalculateRealizedGainOfHoldings() {
        dsl.setReportHoldings();
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

        dsl.verifyRealizedGains("trading:X", "2021", "1");
        dsl.verifyRealizedGains("trading:X", "2022", "3");
    }
}
