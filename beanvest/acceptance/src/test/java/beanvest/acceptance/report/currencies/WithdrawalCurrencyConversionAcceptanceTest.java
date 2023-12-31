package beanvest.acceptance.report.currencies;

import beanvest.acceptance.report.dsl.ReportDsl;
import org.junit.jupiter.api.Test;

public class WithdrawalCurrencyConversionAcceptanceTest {
    protected final ReportDsl dsl = new ReportDsl();

    @Test
    void withdrawalsAreBasedOnCostOfOriginalCurrency() {
        dsl.setCurrency("PLN");
        dsl.setColumns("wths");

        dsl.calculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 price GBP 5 PLN
                2021-01-02 deposit 10
                                
                2021-01-21 price GBP 6 PLN
                2021-01-22 withdraw 10
                """);

        dsl.verifyWithdrawals("trading", "TOTAL", "-50");
    }

    @Test
    void multipleWithdrawalsAreBasedOnCostOfOriginalCurrency() {
        dsl.setCurrency("PLN");
        dsl.setColumns("wths");

        dsl.calculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 price GBP 5 PLN
                2021-01-02 deposit 10
                                
                2021-01-21 price GBP 6 PLN
                2021-01-22 withdraw 2
                2021-01-23 withdraw 3
                """);

        dsl.verifyWithdrawals("trading", "TOTAL", "-25");
    }

    @Test
    void withdrawalsAterSale() {
        dsl.setCurrency("PLN");
        dsl.setColumns("wths");

        dsl.calculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 price GBP 5 PLN constant
                2021-01-02 deposit 10
                2021-01-02 buy 1 X for  10
                2021-01-02 sell 1 X for  10
                2021-01-03 withdraw 10
                """);

        dsl.verifyWithdrawals("trading", "TOTAL", "-50");
    }

    @Test
    void withdrawalsAreBasedOnAverageUnitCostFromTheExchangeRate() {
        dsl.setCurrency("PLN");
        dsl.setColumns("wths");

        dsl.calculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 price GBP 5 PLN
                2021-01-02 deposit 10
                2021-01-03 price GBP 6 PLN
                2021-01-04 deposit 10
                                
                2021-01-22 withdraw 2
                """);

        dsl.verifyWithdrawals("trading", "TOTAL", "-11");
    }

    @Test
    void withdrawalsMightBeLoans() {
        dsl.setCurrency("PLN");
        dsl.setColumns("wths");

        dsl.calculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 price GBP 5 PLN
                2021-01-02 withdraw 10
                2021-01-03 price GBP 6 PLN
                2021-01-04 withdraw 10
                """);

        dsl.verifyWithdrawals("trading", "TOTAL", "-110");
    }
}
