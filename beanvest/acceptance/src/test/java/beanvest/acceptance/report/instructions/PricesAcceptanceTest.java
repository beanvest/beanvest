package beanvest.acceptance.report.instructions;

import beanvest.acceptance.report.ReportDsl;
import org.junit.jupiter.api.Test;
public class PricesAcceptanceTest {
    protected final ReportDsl dsl = new ReportDsl();

    @Test
    void priceCantBeOlderThanAWeek() {
        dsl.setEnd("2021-03-09");
        dsl.setAllowNonZeroExitCodes();
        dsl.setCliOutput();
        dsl.setColumns("value");

        dsl.runCalculateReturns("""
                account trading
                currency GBP

                2021-01-01 deposit and buy 1 X for 10
                2021-03-01 price X 10 GBP
                """);

        dsl.verifyWarningsShown("Price gap is too big for X/GBP on 2021-03-09. Last price is 10 GBP from 2021-03-01");
    }

    @Test
    void priceFromLaterDateCantBeUsed() {
        dsl.setEnd("2021-03-09");
        dsl.setAllowNonZeroExitCodes();
        dsl.setCliOutput();
        dsl.setColumns("value");

        dsl.runCalculateReturns("""
                account trading
                currency GBP

                2021-01-01 deposit and buy 1 X for 10
                2021-03-10 price X 10 GBP
                """);

        dsl.verifyStdErrContains("No price set for X/GBP before or on 2021-03-09");
    }

    @Test
    void constantPriceDoNotNeedToBeUpdated() {
        dsl.setColumns("Value");
        dsl.runCalculateReturns("""
                account property
                currency GBP
                                
                2021-01-01 deposit and buy 1 PROPERTY for 100000
                2021-01-01 price PROPERTY 120000 GBP constant
                """);

        dsl.verifyValue("property", "TOTAL", "120000");
    }
}
