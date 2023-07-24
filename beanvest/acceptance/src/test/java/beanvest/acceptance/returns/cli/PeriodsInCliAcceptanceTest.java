package beanvest.acceptance.returns.cli;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class PeriodsInCliAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @BeforeEach
    void setUp() {
        dsl.setCliOutput();
    }

    @Test
    void calculatesInYearlyIntervalsPeriodic() {
        dsl.setEnd("2022-01-01");
        dsl.setYearly();
        dsl.setColumns("deps");

        dsl.runCalculateReturns("""
                account isa
                currency GBP
                                
                2020-01-01 deposit 100
                2020-06-01 deposit 100
                2021-01-01 deposit 100
                """);

        dsl.verifyOutput("""
                        ╷ 2021  ╷ 2020  ╷
                account │ deps  │ deps  │
                isa     │   300 │   200 │""");
    }

    @Test
    void calculatesInQuarterlyIntervalsPeriodic() {
        dsl.setEnd("2021-05-01");
        dsl.setQuarterly();
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
                account │ deps  │ deps  │ deps  │ deps  │ deps  │
                isa     │   300 │   300 │   300 │   200 │   100 │""");
    }

    @Test
    @Disabled("moving deltas to postprocess")
    void calculatesDeltasInYearlyIntervals() {
        dsl.setEnd("2022-01-01");
        dsl.setYearly();
        dsl.setDeltas();
        dsl.setColumns("deps");

        dsl.runCalculateReturns("""
                account isa
                currency GBP
                                
                2020-01-01 deposit 100
                2020-06-01 deposit 100
                2021-01-01 deposit 100
                """);

        dsl.verifyOutput("""
                        ╷ 2021  ╷ 2020  ╷
                account │ Δdeps │ Δdeps │
                isa     │   100 │   200 │""");
    }

    @Test
    @Disabled
    void showsResultsOnlyStartingFromStartDate() {
        dsl.setEnd("2023-01-01");
        dsl.setStartDate("2021-01-01");
        dsl.setYearly();
        dsl.setDeltas();
        dsl.setColumns("deps");

        dsl.runCalculateReturns("""
                account isa
                currency GBP
                                
                2020-01-01 deposit 100
                2021-01-01 deposit 100
                2022-01-01 deposit 100
                """);

        dsl.verifyOutput("""
                        ╷ 2022  ╷ 2021  ╷
                account │ Δdeps │ Δdeps │
                isa     │   100 │   100 │""");
    }

    @Test
    @Disabled("moving deltas to postprocess")
    void calculatesDeltasInQuarterlyIntervalsPeriodic() {
        dsl.setEnd("2021-05-01");
        dsl.setDeltas();
        dsl.setQuarterly();
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
                account │ Δdeps │ Δdeps │ Δdeps │ Δdeps │ Δdeps │
                isa     │     0 │     0 │   100 │   100 │   100 │""");
    }

    @Test
    @Disabled("processing refactor")
    void calculatesDeltasWithSomeStartingDate() {
        dsl.setStartDate("2021-01-01");
        dsl.setEnd("2022-01-01");
        dsl.setYearly();
        dsl.setDeltas();
        dsl.setColumns("deps");

        dsl.runCalculateReturns("""
                account isa
                currency GBP
                                
                2019-01-01 deposit 100
                """);

        dsl.verifyOutput("""
                        ╷ 2021  ╷
                account │ Δdeps │
                isa     │     0 │""");
    }

    @Test
    void printsTableJustFineIfThereIsNoDataAvailableForSomeOfThePeriods() {
        dsl.setEnd("2021-01-01");
        dsl.setYearly();
        dsl.setColumns("cash");

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
                account       │ cash  │ cash  │
                openedEarlier │    10 │    10 │
                openedLater   │    10 │     … │""");
    }

    @Test
    void printsOneColumnWithDeltasWithoutSpecifiedInterval() {
        dsl.setEnd("2021-01-01");
        dsl.setStartDate("2019-01-01");
        dsl.setDeltas();
        dsl.setColumns("deps");

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
                              ╷ TOTAL ╷
                account       │ Δdeps │
                openedEarlier │    10 │
                openedLater   │    10 │""");
    }

    @Test
    @Disabled("moving deltas to postprocess")
    void shouldCalculateReturnsUntilEndOfLastMonthInDecemberAsWell() {
        dsl.setCurrentDate("2022-12-22");
        dsl.setStartDate("2022-10-01");
        dsl.setEnd("month");
        dsl.setCliOutput();
        dsl.setDeltas();
        dsl.setColumns("deps");
        dsl.setMonthly();

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2022-10-15 deposit 100
                2022-11-15 deposit 100
                2022-12-15 deposit 100
                """);

        dsl.verifyOutput("""
                        ╷ 22m11 ╷ 22m10 ╷
                account │ Δdeps │ Δdeps │
                trading │   100 │   100 │""");
    }
}

