package beanvest.returns.acceptance.stats.cumulative;

import beanvest.returns.ReturnsDsl;
import org.junit.jupiter.api.Test;

public class DividendsAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void calculatesInterestTotal() {
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 100
                2022-06-05 buy 1 X for 10
                2022-07-05 dividend 2 from X
                                
                $$TODAY$$ price X 13
                """);

        dsl.verifyDividends("trading", "TOTAL", "2");
    }

}
