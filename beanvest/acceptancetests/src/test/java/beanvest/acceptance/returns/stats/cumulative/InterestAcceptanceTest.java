package beanvest.acceptance.returns.stats.cumulative;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.Test;

public class InterestAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void calculatesInterestTotal() {
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 100
                2022-06-05 interest 10
                2022-07-05 interest 2.2
                2022-06-05 interest -1
                """);

        dsl.verifyInterest("trading", "TOTAL", "11.2");
    }

}
