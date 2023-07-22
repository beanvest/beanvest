package beanvest.acceptance.returns.instructions;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.Test;

public class BuyAndSellAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void bug_bigDecimalNotEqualsZeroAfterSaleDueToDifferentScale() {
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 2
                2021-01-03 buy 1 X for 2
                2021-01-03 sell 0.5 X for 1.05
                2021-01-03 sell 0.5 X for 1.05
                2021-01-03 withdraw 2.1
                2021-01-03 close
                """);
        dsl.verifyZeroExitCode();
    }
}
