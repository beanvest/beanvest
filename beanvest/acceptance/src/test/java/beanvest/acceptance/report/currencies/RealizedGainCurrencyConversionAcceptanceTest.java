package beanvest.acceptance.report.currencies;

import beanvest.acceptance.report.ReportDsl;
import org.junit.jupiter.api.Test;

public class RealizedGainCurrencyConversionAcceptanceTest {
    protected final ReportDsl dsl = new ReportDsl();

    @Test
    void realizedGainIsBasedOnCommodityGain() {
        dsl.setCurrency("PLN");
        dsl.setColumns("rgain");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 price GBP 5 PLN
                2021-01-02 deposit 1
                2021-01-03 buy 1 X for 1
                2021-01-05 sell 1 X for 2
                """);

        dsl.verifyRealizedGains("trading", "TOTAL", "5");
    }

    @Test
    void realizedGainIsNotBasedOnCurrencyGain() {
        dsl.setCurrency("PLN");
        dsl.setColumns("rgain");
        dsl.setEnd("2021-01-10");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 price GBP 5 PLN
                2021-01-02 deposit 1
                2021-01-03 buy 1 X for 1
                2021-01-04 price GBP 6 PLN
                2021-01-05 sell 1 X for 1
                """);

        dsl.verifyRealizedGains("trading", "TOTAL", "0");
    }
}