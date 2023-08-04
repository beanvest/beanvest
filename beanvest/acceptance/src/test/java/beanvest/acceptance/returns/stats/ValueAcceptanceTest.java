package beanvest.acceptance.returns.stats;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("rework v2")
public class ValueAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void calculatesAccountValue() {
        dsl.setEnd("2023-01-01");
        dsl.setYearly();
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 1
                2021-01-03 buy 1 X for 1
                2021-12-31 price X 3
                2022-12-31 price X 4
                """);

        dsl.verifyValue("trading", "2021", "3");
        dsl.verifyValue("trading", "2022", "4");
    }

    @Test
    @Disabled("doesnt seem to be working")
    void calculatesHoldingValue() {
        dsl.setReportHoldings();
        dsl.setEnd("2023-01-01");
        dsl.setYearly();
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 1
                2021-01-03 buy 2 X for 2
                2021-12-31 price X 2
                2022-12-31 price X 3
                """);

        dsl.verifyValue("trading:X", "2021", "4");
        dsl.verifyValue("trading:X", "2022", "6");
    }
}
