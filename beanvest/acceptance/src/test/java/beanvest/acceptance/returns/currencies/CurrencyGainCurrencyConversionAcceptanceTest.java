package beanvest.acceptance.returns.currencies;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("TODO")
public class CurrencyGainCurrencyConversionAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void currencyGainOnHolding() {
        dsl.setCurrency("PLN");
        dsl.setColumns("cgain");
        dsl.setEnd("2021-01-07");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 price GBP 5 PLN
                2021-01-02 deposit 2
                2021-01-03 buy 2 X for 2
                2021-01-04 price GBP 6 PLN
                """);

        dsl.verifyValue("trading", "TOTAL", "2");
    }

    @Test
    void currencyGainOnCash() {
        dsl.setCurrency("PLN");
        dsl.setColumns("value");
        dsl.setEnd("2021-01-07");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 price GBP 5 PLN
                2021-01-02 deposit 1
                2021-01-04 price GBP 6 PLN
                """);

        dsl.verifyValue("trading", "TOTAL", "1");
    }
}