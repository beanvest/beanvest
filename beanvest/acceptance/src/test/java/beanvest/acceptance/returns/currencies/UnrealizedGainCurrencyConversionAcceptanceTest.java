package beanvest.acceptance.returns.currencies;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("TODO")
public class UnrealizedGainCurrencyConversionAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void unrealizedGainIsBasedOnCommodityGain() {
        dsl.setCurrency("PLN");
        dsl.setColumns("value");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 price GBP 5 PLN
                2021-01-02 deposit 1
                2021-01-03 buy 1 X for 1
                2021-01-04 price X 2 GBP
                """);

        dsl.verifyValue("trading", "TOTAL", "5");
    }

    @Test
    void unrealizedGainIsNotBasedOnCurrencyGain() {
        dsl.setCurrency("PLN");
        dsl.setColumns("value");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 price GBP 5 PLN
                2021-01-02 deposit 1
                2021-01-03 buy 1 X for 1
                2021-01-04 price GBP 6 PLN
                """);

        dsl.verifyValue("trading", "TOTAL", "0");
    }
}