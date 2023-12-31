package beanvest.acceptance.report;

import beanvest.acceptance.report.dsl.ReportDsl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("needs rethinking")
public class CostAcceptanceTest {
    protected final ReportDsl dsl = new ReportDsl();

    @Test
    void netCostOfAccount_withdrawalsReduceIt() {
        dsl.setEnd("2022-01-01");
        dsl.setColumns("cost");
        dsl.calculateReturns("""
                account isa
                currency GBP
                                
                2021-01-01 deposit 100
                2021-01-01 withdraw 20
                """);

        dsl.verifyCost("isa", "TOTAL", "-80");
    }

    @Test
    void netCostOfAccount_withdrawalsReduceItBasedOnAverageCostPerUnit() {
        dsl.setEnd("2022-01-01");
        dsl.setColumns("cost");
        dsl.calculateReturns("""
                account isa
                currency GBP
                                
                2021-01-01 deposit 10
                2021-01-01 interest 10
                2021-01-01 withdraw 5
                """);

        dsl.verifyCost("isa", "TOTAL", "-7.5");
    }

    @Test
    void netCostOfAccount_withHoldings() {
        dsl.setEnd("2022-01-01");
        dsl.setColumns("cost");
        dsl.setReportHoldings();
        dsl.calculateReturns("""
                account isa
                currency GBP
                                
                2021-01-01 deposit 50
                2021-01-01 buy 1 APPL for 20
                """);
        dsl.verifyCost("isa", "TOTAL", "-50");
        dsl.verifyCost("isa:APPL", "TOTAL", "-20");
        dsl.verifyCost("isa:GBP", "TOTAL", "-30");
    }

    @Test
    void netCostOfHolding_shouldBeMoneySpentMinusMoneyEarnedWhenSelling() {
        dsl.setEnd("2022-01-01");
        dsl.setColumns("cost");
        dsl.setReportHoldings();

        dsl.calculateReturns("""
                account isa
                currency GBP
                                
                2021-01-01 deposit 100
                2021-01-01 buy 2 APPL for 50
                2021-01-01 sell 1 APPL for 30
                2021-01-01 dividend 3 from APPL
                """);

        dsl.verifyCost("isa:APPL", "TOTAL", "-25");
    }

    @Test
    void netCostOfCash_withRelizedGain() {
        dsl.setEnd("2022-01-01");
        dsl.setColumns("cost");
        dsl.setReportHoldings();

        dsl.calculateReturns("""
                account isa
                currency GBP
                                
                2021-01-01 deposit 5
                2021-01-01 buy 1 APPL for 5
                2021-01-01 sell 1 APPL for 7
                """);

        dsl.verifyCost("isa", "TOTAL", "-5");
        dsl.verifyCost("isa:APPL", "TOTAL", "0");
        dsl.verifyCost("isa:GBP", "TOTAL", "-5");
    }

    @Test
    void netCostOfCash_interestIsNotAffectingIt() {
        dsl.setEnd("2022-01-01");
        dsl.setColumns("cost");
        dsl.setReportHoldings();

        dsl.calculateReturns("""
                account isa
                currency GBP
                                
                2021-01-01 deposit 5
                2021-01-01 interest 2
                """);

        dsl.verifyCost("isa", "TOTAL", "-5");
        dsl.verifyCost("isa:GBP", "TOTAL", "-5");
    }

    @Test
    void netCostOfCash_platformFeesAreNotAffectingIt() {
        dsl.setEnd("2022-01-01");
        dsl.setColumns("cost");
        dsl.setReportHoldings();

        dsl.calculateReturns("""
                account isa
                currency GBP
                                
                2021-01-01 deposit 5
                2021-01-01 fee 3
                """);

        dsl.verifyCost("isa", "TOTAL", "-5");
        dsl.verifyCost("isa:GBP", "TOTAL", "-5");
    }

    @Test
    void netCostOfCash_transactionFeesAreNotAffectingIt() {
        dsl.setEnd("2022-01-01");
        dsl.setColumns("cost");
        dsl.setReportHoldings();

        dsl.calculateReturns("""
                account isa
                currency GBP
                                
                2021-01-01 deposit 5
                2021-01-01 buy 1 X for 5 with fee 2
                """);

        dsl.verifyCost("isa", "TOTAL", "-5");
        dsl.verifyCost("isa:X", "TOTAL", "-5");
    }

    @Test
    void netCostOfCash_borrowedCapital() {
        dsl.setEnd("2022-01-01");
        dsl.setColumns("cost");
        dsl.setGroupingEnabled();

        dsl.calculateReturns("""
                account leveraged:isa
                currency GBP
                                
                2021-01-01 deposit 100
                ---
                account leveraged:loan
                currency GBP
                                
                2021-01-01 withdraw 100
                """);

        dsl.verifyCost("leveraged:.*", "TOTAL", "0");
        dsl.verifyCost("leveraged:isa", "TOTAL", "-100");
        dsl.verifyCost("leveraged:loan", "TOTAL", "100");
    }

    @Test
    void costOfCashWhenGoingBelowZero() {
        dsl.setColumns("cost,value");
        dsl.setGroupingEnabled();

        dsl.calculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit 5
                2021-01-02 fee 10
                """);

        dsl.verifyCost("trading", "TOTAL", "-5");
        dsl.verifyValue("trading", "TOTAL", "-5");
    }
}
