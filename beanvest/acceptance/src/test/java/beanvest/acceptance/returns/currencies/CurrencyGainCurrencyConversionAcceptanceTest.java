package beanvest.acceptance.returns.currencies;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class CurrencyGainCurrencyConversionAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    @Disabled
    void currencyGainOnHolding() {
        dsl.setCurrency("PLN");
        dsl.setColumns("cgain,ugain,value");
        dsl.setEnd("2021-01-07");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 price GBP 5 PLN
                2021-01-02 deposit 1
                2021-01-03 price GBP 6 PLN
                2021-01-04 deposit 1
                2021-01-05 buy 2 X for 2
                2021-01-06 price GBP 7 PLN
                2021-01-06 price X 1 GBP
                """);

//        dsl.verifyUnrealizedGains("trading", "TOTAL", "0");
        dsl.verifyCurrencyGain("trading", "TOTAL", "3");
    }

    @Test
    void currencyGainOnCash() {
        dsl.setCurrency("PLN");
        dsl.setColumns("CGain");
        dsl.setEnd("2021-01-07");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 price GBP 5 PLN
                2021-01-02 deposit 1
                2021-01-03 price GBP 6 PLN
                2021-01-04 deposit 1
                2021-01-05 price GBP 6.5 PLN
                """);

        dsl.verifyCurrencyGain("trading", "TOTAL", "2");
    }
}