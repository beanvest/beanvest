package beanvest.acceptance.returns.currencies;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("TODO")
public class RealizedGainCurrencyConversionAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void realizedGainIsBasedOnCommodityGain() {
        dsl.setCurrency("PLN");
        dsl.setColumns("value");

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
        dsl.setColumns("value");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 price GBP 5 PLN
                2021-01-02 deposit 1
                2021-01-03 buy 1 X for 1
                2021-01-04 price GBP 6 PLN
                2021-01-05 sell 1 X for 1
                """);

        dsl.verifyValue("trading", "TOTAL", "0");
    }
}