package beanvest.acceptance.report.stats;

import beanvest.acceptance.report.dsl.ReportDsl;
import org.junit.jupiter.api.Test;

public class DepositsAndWithdrawalsAcceptanceTest {
    protected final ReportDsl dsl = new ReportDsl();

    @Test
    void calculatesDeposits() {
        dsl.setColumns("Deps");
        dsl.setReportHoldings();
        dsl.calculateReturns("""
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
        dsl.calculateReturns("""
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
    void calculatesDepositsAndWithdrawals() {
        dsl.setColumns("DW");
        dsl.setReportHoldings();
        dsl.calculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 100
                2022-06-05 withdraw 20
                """);

        dsl.verifyDepositsPlusWithdrawals("trading", "TOTAL", "80");
        dsl.verifyDepositsPlusWithdrawals("trading:GBP", "TOTAL", "80");
    }

    @Test
    void calculatesDepositsAndWithdrawalsPeriodically() {
        dsl.setColumns("DW");
        dsl.setDeltas();
        dsl.setYearly();
        dsl.setReportHoldings();
        dsl.calculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 100
                2022-06-05 deposit 10
                2022-06-05 withdraw 20
                """);

        dsl.verifyDepositsPlusWithdrawalsDelta("trading", "2021", "100");
        dsl.verifyDepositsPlusWithdrawalsDelta("trading:GBP", "2022", "-10");
    }

    @Test
    void holdingsHaveNoDepositsOrWithdrawals() {
        dsl.setColumns("Wths,Deps");
        dsl.calculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 100
                2021-01-02 buy 1 X for 100
                2021-01-02 sell 1 X for 100
                """);

        dsl.verifyDepositsError("trading:X", "TOTAL", "n/a");
        dsl.verifyWithdrawalsError("trading:X", "TOTAL", "n/a");
    }

    @Test
    void aggregatesOnSeveralLevels() {
        dsl.setColumns("Deps");
        dsl.setReportHoldings();
        dsl.setGroupingEnabled();
        dsl.calculateReturns("""
                account shares:fidelity
                currency GBP
                                
                2021-01-02 deposit 100
                2021-01-02 buy 1 APPL for 10
                ---
                account shares:vanguard
                currency GBP
                                
                2021-01-02 deposit 50
                ---
                account savings
                currency GBP
                                
                2021-01-02 deposit 10
                """);

        dsl.verifyDeposits(".*", "TOTAL", "160");
        dsl.verifyDeposits("savings", "TOTAL", "10");
        dsl.verifyDeposits("savings:GBP", "TOTAL", "10");
        dsl.verifyDeposits("shares:.*", "TOTAL", "150");
        dsl.verifyDeposits("shares:fidelity", "TOTAL", "100");
        dsl.verifyDeposits("shares:fidelity:APPL", "TOTAL", "0");
        dsl.verifyDeposits("shares:fidelity:GBP", "TOTAL", "100");
        dsl.verifyDeposits("shares:vanguard", "TOTAL", "50");
        dsl.verifyDeposits("shares:vanguard:GBP", "TOTAL", "50");
    }
}
