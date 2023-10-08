package beanvest.acceptance.report.currencies;

import beanvest.acceptance.report.ReportDsl;
import org.junit.jupiter.api.Test;

public class UnrealizedGainCurrencyConversionAcceptanceTest {
    protected final ReportDsl dsl = new ReportDsl();

    @Test
    void unrealizedGainIsBasedOnCommodityGainInOriginalCurrencyAndAverageCost() {
        dsl.setCurrency("PLN");
        dsl.setColumns("ugain");
        dsl.setEnd("2021-01-10");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 price GBP 5 PLN
                2021-01-02 deposit 1
                2021-01-03 buy 1 X for 1
                2021-01-04 price X 2 GBP
                """);

        dsl.verifyUnrealizedGains("trading", "TOTAL", "5");
    }

    @Test
    void unrealizedGainIsNotBasedOnCurrencyGain() {
        dsl.setCurrency("PLN");
        dsl.setColumns("ugain");
        dsl.setEnd("2021-01-10");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 price GBP 5 PLN
                2021-01-02 deposit 1
                2021-01-03 buy 1 X for 1
                2021-01-04 price X 1 GBP
                2021-01-04 price GBP 6000 PLN
                """);

        dsl.verifyUnrealizedGains("trading", "TOTAL", "0");
    }
}