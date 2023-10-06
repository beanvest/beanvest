package beanvest.acceptance.returns.currencies;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.Test;

public class BalanceCurrencyConversionAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void balanceAssertionsWorkOnOriginalCurrencyOnly() {
        dsl.setCurrency("PLN");
        dsl.setColumns("value");
        dsl.setEnd("2021-01-07");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 price GBP 5 PLN
                2021-01-02 deposit 1
                2021-01-03 balance 1
                """);

        dsl.verifyValue("trading", "TOTAL", "5");
    }

    @Test
    void balanceAssertionsWorkOnOriginalCurrencyOnlyAndShowsErrorsIfMismatch() {
        dsl.setCurrency("PLN");
        dsl.setColumns("value");
        dsl.setAllowNonZeroExitCodes();

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 price GBP 5 PLN
                2021-01-02 deposit 1
                2021-01-03 balance 5
                """);

        dsl.verifyNonZeroExitCode();
    }
}