package beanvest.acceptance.report.stats.delta;

import beanvest.acceptance.report.dsl.ReportDsl;
import org.junit.jupiter.api.Test;
public class DepositsAndWithdrawalsDeltaAcceptanceTest {
    protected final ReportDsl dsl = new ReportDsl();

    @Test
    void calculatesDepositsYearlyDelta() {
        dsl.setColumns("Deps");
        dsl.setDeltas();
        dsl.setEnd("2023-01-01");
        dsl.setYearly();
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 100
                2021-06-02 deposit 50
                                
                2022-01-03 deposit 30
                2022-06-05 deposit 33
                """);

        dsl.verifyDepositsDelta("trading", "2021", "150");
        dsl.verifyDepositsDelta("trading", "2022", "63");
    }

    @Test
    void calculatesWithdrawalsYearlyDelta() {
        dsl.setColumns("Wths");
        dsl.setDeltas();
        dsl.setEnd("2023-01-01");
        dsl.setYearly();
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 100
                2021-06-05 withdraw 5
                2021-07-05 withdraw 2
                                
                2022-06-05 withdraw 10
                2022-06-05 withdraw 12.31
                """);

        dsl.verifyWithdrawalsDelta("trading", "2021", "-7");
        dsl.verifyWithdrawalsDelta("trading", "2022", "-22.31");
    }
}
