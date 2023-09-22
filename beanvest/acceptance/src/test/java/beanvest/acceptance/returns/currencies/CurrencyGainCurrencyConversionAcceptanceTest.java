package beanvest.acceptance.returns.currencies;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class CurrencyGainCurrencyConversionAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void currencyGainOnHolding() {
        dsl.setCurrency("PLN");
        dsl.setColumns("cgain,ugain,value");
        dsl.setEnd("2021-01-07");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 price GBP 5 PLN
                2021-01-02 deposit 1
                2021-01-01 price GBP 6 PLN
                2021-01-02 deposit 1
                2021-01-03 buy 2 X for 2
                2021-01-04 price GBP 7 PLN
                2021-01-04 price X 1 GBP
                """);

        dsl.verifyValue("trading", "TOTAL", "14");
        dsl.verifyUnrealizedGains("trading", "TOTAL", "0");
        dsl.verifyCurrencyGain("trading", "TOTAL", "3");
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