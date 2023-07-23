package beanvest.acceptance.returns.stats.cumulative;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.Test;

public class DepositsAndWithdrawalsAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void calculatesDepositsTotal() {
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 100
                2022-06-05 deposit 33
                """);

        dsl.verifyDeposits("trading", "TOTAL", "133");
    }


    @Test
    void calculatesWithdrawalsTotal() {
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 100
                2022-06-05 withdraw 10
                2022-06-05 withdraw 12.31
                """);

        dsl.verifyWithdrawals("trading", "TOTAL", "-22.31");
    }

}
