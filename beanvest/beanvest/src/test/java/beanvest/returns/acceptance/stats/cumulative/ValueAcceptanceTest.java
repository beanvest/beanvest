package beanvest.returns.acceptance.stats.cumulative;

import beanvest.returns.ReturnsDsl;
import org.junit.jupiter.api.Test;

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
}