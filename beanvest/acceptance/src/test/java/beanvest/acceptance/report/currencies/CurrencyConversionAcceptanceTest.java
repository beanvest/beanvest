package beanvest.acceptance.report.currencies;

import beanvest.acceptance.report.dsl.ReportDsl;
import org.junit.jupiter.api.Test;

public class CurrencyConversionAcceptanceTest {
    protected final ReportDsl dsl = new ReportDsl();

    @Test
    void conversionToSourceCurrencyDoesNothing() {
        dsl.setCurrency("PLN");
        dsl.setColumns("deps");

        dsl.calculateReturns("""
                account tradingPLN
                currency PLN
                                
                2021-01-01 deposit 50
                ---
                account tradingGBP
                currency GBP
                                    
                2021-01-01 price GBP 5 PLN
                2021-01-02 deposit 20
                """);

        dsl.verifyDeposits("tradingGBP", "TOTAL", "100");
        dsl.verifyDeposits("tradingPLN", "TOTAL", "50");
    }
}