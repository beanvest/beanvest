package beanvest.acceptance.report;

import beanvest.acceptance.report.dsl.ReportDsl;
import org.junit.jupiter.api.Test;

public class StartAndEndDateAcceptanceTest {
    protected final ReportDsl dsl = new ReportDsl();

    @Test
    void usesCurrentDateByDefault() {
        dsl.setColumns("AGain");
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit and buy 1 VLS for 100
                $$TODAY$$ price VLS 120
                """);

        dsl.verifyAccountGainIsPositive("trading", "TOTAL");
        dsl.verifyEndDateIsToday();
    }

    @Test
    void endDateForReturnsCalculationCanBeProvided() {
        dsl.setEnd("2022-05-01");
        dsl.setColumns("profit");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit and buy 1 VLS for 100
                2022-05-01 price VLS 80 GBP
                2022-05-02 price VLS 100 GBP
                """);

        dsl.verifyProfit("trading", "TOTAL", "-20");
    }

    @Test
    void startDateForStatsCalculationCanBeProvided() {
        dsl.setStartDate("2021-03-01");
        dsl.setColumns("again");
        dsl.setDeltas();

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit and buy 1 VLS for 100
                2021-02-28 price VLS 80 GBP
                $$TODAY$$  price VLS 100 GBP
                """);

        dsl.verifyAccountGainDelta("trading", "TOTAL", "20");
    }

    @Test
    void accountsClosedAfterStartDateAreVisibleWhenRetrievingDeltas() {
        dsl.setStartDate("2021-02-01");
        dsl.setDeltas();

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit 100
                2021-02-02 withdraw 100
                2021-02-03 close
                ---
                account investing
                currency GBP
                                
                2021-05-01 deposit 100
                ---
                """);

        dsl.verifyHasStats("investing");
        dsl.verifyHasStats("trading");
    }

    @Test
    void accountsOpenAfterEndDateAreNotVisible() {
        dsl.setEnd("2016-06-01");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit 100
                2021-02-01 withdraw 100
                2021-02-03 close
                ---
                account investing
                currency GBP
                                
                2015-02-02 deposit 100
                ---
                """);

        dsl.verifyHasStats("investing");
        dsl.verifyHasNoStats("trading");
    }

    @Test
    void accountsClosedBeforeStartDateAreNotVisibleWhenQueryingDeltas() {
        dsl.setStartDate("2022-01-01");
        dsl.setColumns("deps");
        dsl.setDeltas();

        dsl.runCalculateReturns("""
                account alreadyClosed
                currency GBP
                                
                2021-01-01 deposit 100
                2021-02-01 withdraw 100
                2021-02-03 close
                ---
                account opened
                currency GBP
                                
                2022-02-02 deposit 100
                ---
                """);

        dsl.verifyHasStats("opened");
        dsl.verifyHasNoStats("alreadyClosed");
    }

    @Test
    void accountsClosedBeforeStartDateAreNotVisibleWhenQueryingCumulativeStats() {
        dsl.setStartDate("2022-01-01");
        dsl.setColumns("deps");

        dsl.runCalculateReturns("""
                account alreadyClosed
                currency GBP
                                
                2021-01-01 deposit 100
                2021-02-01 withdraw 100
                2021-02-03 close
                ---
                account opened
                currency GBP
                                
                2022-02-02 deposit 100
                ---
                """);

        dsl.verifyHasStats("opened");
        dsl.verifyHasStats("alreadyClosed");
    }
}
