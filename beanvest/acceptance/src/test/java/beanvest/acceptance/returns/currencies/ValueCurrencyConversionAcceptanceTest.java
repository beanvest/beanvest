package beanvest.acceptance.returns.currencies;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("TODO")
public class ValueCurrencyConversionAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void cashValueIsBasedOnCurrentExchangeRate() {
        dsl.setCurrency("PLN");
        dsl.setColumns("value");
        dsl.setEnd("2021-01-05");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 price GBP 5 PLN
                2021-01-02 deposit 1
                2021-01-03 price GBP 6 PLN
                """);

        dsl.verifyValue("trading", "TOTAL", "6");
    }

    @Test
    void holdingValueIsBasedOnTargetCurrencyExchangeRate() {
        dsl.setCurrency("PLN");
        dsl.setColumns("value");
        dsl.setEnd("2021-01-05");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 price GBP 5 PLN
                2021-01-02 deposit 1
                2021-01-02 buy 1 X for 1
                2021-01-03 price X 1 GBP
                2021-01-03 price GBP 6 PLN
                """);

        dsl.verifyValue("trading", "TOTAL", "6");
    }
}