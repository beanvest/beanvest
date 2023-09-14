package beanvest.acceptance.returns.currencies;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class DepositsAndWithdrawalsCurrencyConversionAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void depositsAreConvertedBasedOnExchangeRateWhenItHappened() {
        dsl.setCurrency("PLN");
        dsl.setColumns("deps");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                2021-01-01 price GBP 5 PLN
                2021-01-02 deposit 10
                2021-01-21 price GBP 6 PLN                
                                
                """);

        dsl.verifyDeposits("trading", "TOTAL", "50");
    }

    @Test
    void multipleDeposits() {
        dsl.setCurrency("PLN");
        dsl.setColumns("deps");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 price GBP 5 PLN
                2021-01-02 deposit 10
                2021-01-21 price GBP 6 PLN
                2021-01-22 deposit 10
                """);

        dsl.verifyDeposits("trading", "TOTAL", "110");
    }

    @Test
    void withdrawalsAreBasedOnCostOfOriginalCurrency() {
        dsl.setCurrency("PLN");
        dsl.setColumns("deps,wths");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 price GBP 5 PLN
                2021-01-02 deposit 10
                                
                2021-01-21 price GBP 6 PLN
                2021-01-22 withdraw 10
                """);

        dsl.verifyDeposits("trading", "TOTAL", "50");
        dsl.verifyWithdrawals("trading", "TOTAL", "50");
    }

    @Test
    void multipleWithdrawalsAreBasedOnCostOfOriginalCurrency() {
        dsl.setCurrency("PLN");
        dsl.setColumns("deps,wths");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 price GBP 5 PLN
                2021-01-02 deposit 10
                                
                2021-01-21 price GBP 6 PLN
                2021-01-22 withdraw 2
                2021-01-23 withdraw 3
                """);

        dsl.verifyDeposits("trading", "TOTAL", "50");
        dsl.verifyWithdrawals("trading", "TOTAL", "25");
    }

    @Test
    void withdrawalsAreBasedOnCostOfOriginalCurrencyThatIsAveragedAcrossTheDeposits() {
        dsl.setCurrency("PLN");
        dsl.setColumns("deps,wths");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 price GBP 5 PLN     
                2021-01-02 deposit 10
                2021-01-03 price GBP 6 PLN
                2021-01-04 deposit 10
                                
                2021-01-22 withdraw 2
                """);

        dsl.verifyDeposits("trading", "TOTAL", "110");
        dsl.verifyWithdrawals("trading", "TOTAL", "11");
    }

    @Test
    @Disabled("wip")
    void interestIsBasedOnCurrentExchangeRate() {
        dsl.setCurrency("PLN");
        dsl.setColumns("deps,wths");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 price GBP 5 PLN
                2021-01-02 deposit 1
                2021-01-03 price GBP 6 PLN
                2021-01-04 interest 1
                2021-01-05 withdraw 2
                """);

        dsl.verifyDeposits("trading", "TOTAL", "5");
        dsl.verifyWithdrawals("trading", "TOTAL", "11");
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