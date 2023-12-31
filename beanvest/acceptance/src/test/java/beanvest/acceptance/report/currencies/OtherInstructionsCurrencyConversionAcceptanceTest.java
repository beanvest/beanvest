package beanvest.acceptance.report.currencies;

import beanvest.acceptance.report.dsl.ReportDsl;
import org.junit.jupiter.api.Test;

public class OtherInstructionsCurrencyConversionAcceptanceTest {
    protected final ReportDsl dsl = new ReportDsl();

    @Test
    void balanceAssertionsWorkOnOriginalCurrencyOnly() {
        dsl.setCurrency("PLN");
        dsl.setColumns("value");
        dsl.setEnd("2021-01-07");

        dsl.calculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 price GBP 5 PLN
                2021-01-02 deposit 2
                2021-01-03 balance 2
                2021-01-04 withdraw 1
                2021-01-05 balance 1
                """);

        dsl.verifyValue("trading", "TOTAL", "5");
    }

    @Test
    void accountsCanBeClosedJustFine() {
        dsl.setCurrency("PLN");
        dsl.setColumns("value");
        dsl.setEnd("2021-01-07");

        dsl.calculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 price GBP 5 PLN
                2021-01-02 deposit 2
                2021-01-03 withdraw 2
                2021-01-05 close
                """);

        dsl.verifyValue("trading", "TOTAL", "0");
    }

    @Test
    void balanceAssertionsWorkOnOriginalCurrencyOnlyAndShowsErrorsIfMismatch() {
        dsl.setCurrency("PLN");
        dsl.setColumns("value");
        dsl.setAllowNonZeroExitCodes();

        dsl.calculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 price GBP 5 PLN
                2021-01-02 deposit 1
                2021-01-03 balance 5
                """);

        dsl.verifyNonZeroExitCode();
    }
}