package beanvest.acceptance.report.currencies;

import beanvest.acceptance.report.dsl.ReportDsl;
import org.junit.jupiter.api.Test;

public class DepositsCurrencyConversionAcceptanceTest {
    protected final ReportDsl dsl = new ReportDsl();

    @Test
    void depositsAreConvertedBasedOnExchangeRateWhenItHappened() {
        dsl.setCurrency("PLN");
        dsl.setColumns("deps");

        dsl.calculateReturns("""
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

        dsl.calculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 price GBP 5 PLN
                2021-01-02 deposit 10
                2021-01-21 price GBP 6 PLN
                2021-01-22 deposit 10
                """);

        dsl.verifyDeposits("trading", "TOTAL", "110");
    }
}