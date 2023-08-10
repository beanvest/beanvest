package beanvest.acceptance.returns.stats;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class DepositsAndWithdrawalsAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void calculatesDeposits() {
        dsl.setColumns("Deps");
        dsl.setReportHoldings();
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 100
                2022-06-05 deposit 33
                """);

        dsl.verifyDeposits("trading", "TOTAL", "133");
        dsl.verifyDeposits("trading:GBP", "TOTAL", "133");
    }

    @Test
    void calculatesWithdrawals() {
        dsl.setColumns("Wths");
        dsl.setReportHoldings();
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 100
                2022-06-05 withdraw 20
                2022-06-05 withdraw 10
                """);

        dsl.verifyWithdrawals("trading", "TOTAL", "-30");
        dsl.verifyWithdrawals("trading:GBP", "TOTAL", "-30");
    }

    @Test
    void holdingsHaveNoDepositsOrWithdrawals() {
        dsl.setColumns("Wths,Deps");
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 100
                2021-01-02 buy 1 X for 100
                2021-01-02 sell 1 X for 100
                """);

        dsl.verifyDepositsError("trading:X", "TOTAL", "n/a");
        dsl.verifyWithdrawalsError("trading:X", "TOTAL", "n/a");
    }
}
