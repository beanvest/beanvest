package beanvest.acceptance.returns.currencies;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("TODO")
public class ValueCurrencyConversionAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void unrealizedGainIsReducedAfterSellingPart() {
        dsl.setCurrency("PLN");
        dsl.setColumns("value");
        dsl.setYearly();

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 price GBP 5 PLN
                2021-01-01 price X 1 GBP
                2021-01-02 deposit 2
                2021-01-03 buy 2 X for 2
                
                2021-12-31 price GBP 5 PLN
                2021-12-31 price X 2 GBP
                """);

        dsl.verifyUnrealizedGains("trading", "2021", "10");
    }
}