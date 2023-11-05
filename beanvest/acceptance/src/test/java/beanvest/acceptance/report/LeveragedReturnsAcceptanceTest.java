package beanvest.acceptance.report;

import beanvest.acceptance.report.dsl.ReportDsl;
import org.junit.jupiter.api.Test;

public class LeveragedReturnsAcceptanceTest {
    protected final ReportDsl dsl = new ReportDsl();

    @Test
    void shouldCalculateLeveragedReturnsWithOnlyInclusionOfCreditAccount() {
        dsl.setCurrentDate("2023-01-01");
        dsl.setGroupingEnabled();
        dsl.setColumns("xirr");

        dsl.runCalculateReturns("""
                account saving
                currency GBP
                                
                2022-01-01 deposit 100
                2022-01-01 deposit 100
                2022-12-30 interest 20
                ---
                account loan
                currency GBP
                                
                2022-01-01 withdraw 100
                """);
        dsl.verifyXirrCumulative("saving", "TOTAL", "10");
        dsl.verifyXirrCumulative(".*", "TOTAL", "20");
    }

    @Test
    void shouldCalculateResultsWhenLeveragedFromAnyArbitraryMoment() {
        dsl.setCurrentDate("2023-01-01");
        dsl.setGroupingEnabled();
        dsl.setColumns("xirr");

        dsl.runCalculateReturns("""
                account saving
                currency GBP
                                
                2022-01-01 deposit 100
                2022-01-01 deposit 100
                2022-12-30 interest 20
                ---
                account loan
                currency GBP
                                
                2022-06-01 withdraw 100
                """);
        dsl.verifyXirrCumulative("saving", "TOTAL", "10");
        dsl.verifyXirrCumulative(".*", "TOTAL", "14");
    }

    @Test
    void shouldCalculateReturnsIfLeverageIsLargerThanCapital() {
        dsl.setCurrentDate("2023-01-01");
        dsl.setGroupingEnabled();
        dsl.setColumns("xirr");

        dsl.runCalculateReturns("""
                account saving
                currency GBP
                                
                2022-01-01 deposit 100
                2022-01-01 deposit 200
                2022-12-30 interest 30
                ---
                account loan
                currency GBP
                                
                2022-01-01 withdraw 200
                """);
        dsl.verifyXirrCumulative("saving", "TOTAL", "10");
        dsl.verifyXirrCumulative(".*", "TOTAL", "30");
    }
}
