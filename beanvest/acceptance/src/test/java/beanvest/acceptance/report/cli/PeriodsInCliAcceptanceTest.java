package beanvest.acceptance.report.cli;

import beanvest.acceptance.report.dsl.ReportDsl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PeriodsInCliAcceptanceTest {
    protected final ReportDsl dsl = new ReportDsl();

    @BeforeEach
    void setUp() {
        dsl.setCliOutput();
    }

    @Test
    void calculatesInYearlyIntervalsPeriodic() {
        dsl.setEnd("2022-01-01");
        dsl.setYearly();
        dsl.setColumns("deps");
        dsl.setGroupingDisabled();

        dsl.runCalculateReturns("""
                account isa
                currency GBP
                                
                2020-01-01 deposit 100
                2020-06-01 deposit 100
                2021-01-01 deposit 100
                """);

        dsl.verifyOutput("""
                        ╷ 2021  ╷ 2020  ╷
                Account │ Deps  │ Deps  │
                isa     │   300 │   200 │""");
    }

    @Test
    void calculatesInQuarterlyIntervalsPeriodic() {
        dsl.setEnd("2021-05-01");
        dsl.setQuarterly();
        dsl.setGroupingDisabled();
        dsl.setColumns("deps");

        dsl.runCalculateReturns("""
                account isa
                currency GBP
                                
                2020-01-01 deposit 100
                2020-06-30 deposit 100
                2020-07-01 deposit 100
                """);

        dsl.verifyOutput("""
                        ╷ 21q1  ╷ 20q4  ╷ 20q3  ╷ 20q2  ╷ 20q1  ╷
                Account │ Deps  │ Deps  │ Deps  │ Deps  │ Deps  │
                isa     │   300 │   300 │   300 │   200 │   100 │""");
    }

    @Test
    void calculatesDeltasInYearlyIntervals() {
        dsl.setEnd("2022-01-01");
        dsl.setYearly();
        dsl.setColumns("Deps");
        dsl.setDeltas();
        dsl.setGroupingDisabled();

        dsl.runCalculateReturns("""
                account isa
                currency GBP
                                
                2020-01-01 deposit 100
                2020-06-01 deposit 100
                2021-01-01 deposit 100
                """);

        dsl.verifyOutput("""
                        ╷ 2021  ╷ 2020  ╷
                Account │ pDeps │ pDeps │
                isa     │   100 │   200 │""");
    }

    @Test
    void showsResultsOnlyStartingFromStartDate() {
        dsl.setEnd("2023-01-01");
        dsl.setStartDate("2021-01-01");
        dsl.setYearly();
        dsl.setDeltas();
        dsl.setColumns("deps");
        dsl.setGroupingDisabled();

        dsl.runCalculateReturns("""
                account isa
                currency GBP
                                
                2020-01-01 deposit 100
                2021-01-01 deposit 100
                2022-01-01 deposit 100
                """);

        dsl.verifyOutput("""
                        ╷ 2022  ╷ 2021  ╷
                Account │ pDeps │ pDeps │
                isa     │   100 │   100 │""");
    }

    @Test
    void calculatesDeltasInQuarterlyIntervalsPeriodic() {
        dsl.setEnd("2021-05-01");
        dsl.setQuarterly();
        dsl.setGroupingDisabled();
        dsl.setColumns("Deps");
        dsl.setDeltas();

        dsl.runCalculateReturns("""
                account isa
                currency GBP
                                
                2020-01-01 deposit 100
                2020-06-30 deposit 100
                2020-07-01 deposit 100
                """);

        dsl.verifyOutput("""
                        ╷ 21q1  ╷ 20q4  ╷ 20q3  ╷ 20q2  ╷ 20q1  ╷
                Account │ pDeps │ pDeps │ pDeps │ pDeps │ pDeps │
                isa     │     0 │     0 │   100 │   100 │   100 │""");
    }

    @Test
    void calculatesDeltasWithSomeStartingDate() {
        dsl.setStartDate("2021-01-01");
        dsl.setEnd("2022-01-01");
        dsl.setYearly();
        dsl.setGroupingDisabled();
        dsl.setColumns("Deps");
        dsl.setDeltas();

        dsl.runCalculateReturns("""
                account isa
                currency GBP
                                
                2019-01-01 deposit 100
                2021-02-01 deposit 50
                """);

        dsl.verifyOutput("""
                        ╷ 2021  ╷
                Account │ pDeps │
                isa     │    50 │""");
    }

    @Test
    void printsTableJustFineIfThereIsNoDataAvailableForSomeOfThePeriods() {
        dsl.setEnd("2021-01-01");
        dsl.setYearly();
        dsl.setColumns("value");
        dsl.setGroupingDisabled();

        dsl.runCalculateReturns("""
                account openedEarlier
                currency GBP

                2019-06-01 deposit 10
                ---
                account openedLater
                
                currency GBP

                2020-06-01 deposit 10
                """);

        dsl.verifyOutput("""
                              ╷ 2020  ╷ 2019  ╷
                Account       │ Value │ Value │
                openedEarlier │    10 │    10 │
                openedLater   │    10 │     … │""");
    }

    @Test
    void shouldCalculateReturnsUntilEndOfLastMonthInDecemberAsWell() {
        dsl.setCliOutput();
        dsl.setColumns("Deps");
        dsl.setDeltas();
        dsl.setGroupingDisabled();
        dsl.setMonthly();

        dsl.setStartDate("2022-10-01");
        dsl.setEnd("month");
        dsl.setCurrentDate("2022-12-22");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2022-10-15 deposit 100
                2022-11-15 deposit 100
                2022-12-15 deposit 100
                """);

        dsl.verifyOutput("""
                        ╷ 22m11 ╷ 22m10 ╷
                Account │ pDeps │ pDeps │
                trading │   100 │   100 │""");
    }
}

