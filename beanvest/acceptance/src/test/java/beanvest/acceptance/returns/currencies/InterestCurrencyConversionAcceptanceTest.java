package beanvest.acceptance.returns.currencies;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class InterestCurrencyConversionAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void interestIsBasedOnPastExchangeRate() {
        dsl.setCurrency("PLN");
        dsl.setColumns("intr");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 price GBP 5 PLN
                2021-01-02 deposit 1
                2021-01-03 price GBP 6 PLN
                2021-01-04 interest 1
                """);

        dsl.verifyInterest("trading", "TOTAL", "5");
    }

    @Test
    void negativeInterestReducesHoldingProportionallyBasedOnAverageCost() {
        dsl.setCurrency("PLN");
        dsl.setColumns("intr");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 price GBP 5 PLN
                2021-01-02 deposit 10
                
                2021-01-03 price GBP 6 PLN
                2021-01-04 interest -1
                                
                2021-01-07 withdraw 9
                """);

        dsl.verifyInterest("trading", "TOTAL", "-5");
    }

}