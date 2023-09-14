package beanvest.acceptance.returns.currencies;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.Test;

public class InterestCurrencyConversionAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void interestIsBasedOnCurrentExchangeRate() {
        dsl.setCurrency("PLN");
        dsl.setColumns("deps,wths,intr");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 price GBP 5 PLN
                2021-01-02 deposit 1
                2021-01-03 price GBP 6 PLN
                2021-01-04 interest 2
                2021-01-05 withdraw 3
                """);

        dsl.verifyInterest("trading", "TOTAL", "12");
        dsl.verifyDeposits("trading", "TOTAL", "5");
        dsl.verifyWithdrawals("trading", "TOTAL", "17");
    }

    @Test
    void interestIsBasedOnCurrentExchangeRates() {
        dsl.setCurrency("PLN");
        dsl.setColumns("deps,wths,intr");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 price GBP 5 PLN
                2021-01-02 deposit 1
                                
                2021-01-03 price GBP 6 PLN
                2021-01-04 interest 1
                2021-01-05 price GBP 7 PLN
                2021-01-06 interest 1
                                
                2021-01-07 withdraw 3
                """);

        dsl.verifyInterest("trading", "TOTAL", "13");
        dsl.verifyDeposits("trading", "TOTAL", "5");
        dsl.verifyWithdrawals("trading", "TOTAL", "18"); //5+6+7
    }

    @Test
    void negativeInterestReducesHoldingProportionallyBasedOnAverageCost() {
        dsl.setCurrency("PLN");
        dsl.setColumns("deps,wths,intr");

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
        dsl.verifyDeposits("trading", "TOTAL", "50");
        dsl.verifyWithdrawals("trading", "TOTAL", "45");
    }

/**
 * general idea:
 * dep 1gbp (5 pln)
 * dep 1gbp (6 pln)
 * buy 1X for 1 gbp (5.5 pln)
 * div 1 (7pln)
 * sell 1X for 2 gbp (11 pln)
 * wth 2gbp (11 pln) (realized gain (CGain -> EGain?))
 */
}