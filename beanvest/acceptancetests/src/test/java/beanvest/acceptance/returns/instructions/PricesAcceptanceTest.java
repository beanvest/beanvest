package beanvest.acceptance.returns.instructions;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("processing refactor")
public class PricesAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void priceCantBeOlderThanAWeek() {
        dsl.setEnd("2021-03-09");
        dsl.setAllowNonZeroExitCodes();
        dsl.setCliOutput();

        dsl.runCalculateReturns("""
                account trading
                currency GBP

                2021-01-01 deposit and buy 1 X for 10
                2021-03-01 price X 10 GBP
                """);

        dsl.verifyWarningsShown("Price gap is too big for X/GBP on 2021-03-09. Last price is 10 GBP from 2021-03-01.");
    }

    @Test
    void priceFromLaterDateCantBeUsed() {
        dsl.setEnd("2021-03-09");
        dsl.setAllowNonZeroExitCodes();
        dsl.setCliOutput();

        dsl.runCalculateReturns("""
                account trading
                currency GBP

                2021-01-01 deposit and buy 1 X for 10
                2021-03-10 price X 10 GBP
                """);

        dsl.verifyStdErrContains("No price set for X/GBP on 2021-03-09");
    }
}
