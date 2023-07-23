package beanvest.acceptance.returns.stats.cumulative;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.Test;

public class UnrealizedGainAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void calculatesUnrealizedGain() {
        dsl.setEnd("2021-01-06");
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 10
                2021-01-03 buy 1 X for 10
                2021-01-04 price X 11
                                
                """);

        dsl.verifyUnrealizedGains("trading", "TOTAL", "1");
    }

    @Test
    void unrealizedGainAfterPartialSale() {
        dsl.setEnd("2021-01-06");
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 10
                2021-01-03 buy 2 X for 10
                2021-01-04 price X 6
                2021-01-05 sell 1 X for 7
                """);

        dsl.verifyUnrealizedGains("trading", "TOTAL", "1");
    }

    @Test
    void unrealizedGainIsZeroedWhenSelling() {
        dsl.setEnd("2021-01-06");
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 10
                2021-01-03 buy 1 X for 10
                2021-01-04 price X 12
                2021-01-05 sell 1 X for 11
                """);

        dsl.verifyUnrealizedGains("trading", "TOTAL", "0");
    }

    @Test
    void calculatesUnrealizedGainFromMultipleSecurities() {
        dsl.setEnd("2021-01-06");
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 15
                2021-01-03 buy 1 X for 5
                2021-01-03 buy 2 Y for 10
                2021-01-04 price X 5.5
                2021-01-04 price Y 5.1
                                
                """);

        dsl.verifyUnrealizedGains("trading", "TOTAL", "0.7");
    }
}