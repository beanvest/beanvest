package beanvest.acceptance.returns.currencies;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.Test;

public class FeeCurrencyConversionAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void feesReduceHoldingProportionallyKeepingAverageCost() {
        dsl.setCurrency("PLN");
        dsl.setColumns("deps,wths,fees");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 price GBP 5 PLN
                2021-01-02 deposit 10
                
                2021-01-03 price GBP 6 PLN
                2021-01-04 fee 1
                                
                2021-01-07 withdraw 9
                """);

        dsl.verifyFeesTotal("trading", "TOTAL", "5");
        dsl.verifyDeposits("trading", "TOTAL", "50");
        dsl.verifyWithdrawals("trading", "TOTAL", "45");
    }


    @Test
    void feesReduceHoldingProportionallyByAverageCost() {
        dsl.setCurrency("PLN");
        dsl.setColumns("deps,wths,fees");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 price GBP 5 PLN
                2021-01-02 deposit 5
                2021-01-03 price GBP 6 PLN
                2021-01-04 deposit 5
                2021-01-05 fee 1
                2021-01-07 withdraw 9
                """);

        dsl.verifyFeesTotal("trading", "TOTAL", "5.5");
        dsl.verifyDeposits("trading", "TOTAL", "55");
        dsl.verifyWithdrawals("trading", "TOTAL", "49.5");
    }
}