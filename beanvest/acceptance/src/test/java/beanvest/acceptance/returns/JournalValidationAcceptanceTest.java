package beanvest.acceptance.returns;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("rework v2")
public class JournalValidationAcceptanceTest {
    protected ReturnsDsl dsl = new ReturnsDsl();
    @Test
    void shouldNotShowStatsForPeriodIfPriceGapIsTooBig() {
        dsl.setEnd("2021-01-01");
        dsl.setYearly();

        dsl.runCalculateReturns("""
                account pension
                currency GBP
                                
                2020-01-01 deposit and buy 1 MSFT for 1000
                2020-12-20 price MSFT 1001 GBP
                """);

        dsl.verifyCash("pension", "2020", "0");
        dsl.verifyXirrNotPresent("pension", "2020");
        dsl.verifyXirrError("pension", "2020", "PRICE_NEEDED");
    }

    @Test
    void shouldWarnIfPriceGapIsTooBig() {
        dsl.setEnd("2022-01-01");
        dsl.setYearly();

        dsl.runCalculateReturns("""
                account pension
                currency GBP
                                
                2019-01-01 deposit and buy 1 MSFT for 1000
                2019-12-31 price MSFT 1001 GBP
                2021-12-31 price MSFT 1002 GBP
                """);

        dsl.verifyResultErrorShown("pension", "2020", "PRICE_NEEDED");
        dsl.verifyResultErrorShown("pension", "2021", "PRICE_NEEDED");
        dsl.verifyWarningsShown("Price gap is too big for MSFT/GBP on 2020-12-31. Last price is 1001 GBP from 2019-12-31.");
    }

    @Test
    void failsIfCurrencyNotSpecified() {
        dsl.setEnd("2021-12-31");
        dsl.setAllowNonZeroExitCodes();

        dsl.runCalculateReturns("""
                account pension
                                
                2019-01-01 deposit 10
                """);

        dsl.verifyNonZeroExitCode();
        dsl.verifyReturnedAnError("Currency not specified but needed in journal");
    }
}
