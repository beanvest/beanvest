package beanvest.acceptance.returns.cli;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CliStatsAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @BeforeEach
    void setUp() {
        dsl.setCliOutput();
    }

    @Test
    void calculatesFeesTotal() {
        dsl.setColumns("fees");
        dsl.setExact();
        dsl.setGroupingDisabled();

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 100
                2021-01-03 buy 1 X for 50 with fee 1
                2021-02-04 sell 1 X for 50 with fee 2
                2021-02-05 fee 1.2
                                
                2022-01-03 buy 1 X for 50 with fee 1
                2022-02-04 sell 1 X for 50 with fee 1.1
                2022-02-05 fee 1.2
                """);


        dsl.verifyOutput("""                                
                account  fees
                trading  -7.50""");
    }

    @Test
    void calculatesFeesYearlyDelta() {
        dsl.setEnd("2023-01-01");
        dsl.setYearly();
        dsl.setDeltas();
        dsl.setGroupingDisabled();
        dsl.setColumns("fees");
        dsl.setExact();
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 100
                2021-01-03 buy 1 X for 50 with fee 1
                2021-02-04 sell 1 X for 50 with fee 2
                2021-02-05 fee 1.2
                                
                2022-01-03 buy 1 X for 50 with fee 1
                2022-02-04 sell 1 X for 50 with fee 1.1
                2022-02-05 fee 1.2
                """);

        dsl.verifyOutput("""         
                        ╷ 2022  ╷ 2021  ╷
                account │ Δfees │ Δfees │
                trading │ -3.30 │ -4.20 │""");
    }

    @Test
    void calculatesInterestYearlyDelta() {
        dsl.setEnd("2023-01-01");
        dsl.setDeltas();
        dsl.setColumns("aGain");
        dsl.setGroupingDisabled();
        dsl.setYearly();
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 100
                2021-06-05 interest 5
                2021-07-05 interest 2
                                
                2022-06-05 interest 10
                2022-07-13 interest -1
                """);

        dsl.verifyOutput("""         
                        ╷ 2022   ╷ 2021   ╷
                account │ ΔaGain │ ΔaGain │
                trading │      9 │      7 │""");
    }
}

