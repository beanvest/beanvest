package beanvest.acceptance.returns.cli;

import beanvest.acceptance.returns.ReturnsDsl;
import beanvest.lib.testing.WorkInProgress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CliHoldingsStatsAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @BeforeEach
    void setUp() {
        dsl.setCliOutput();
    }

    @Test
    void shouldCalculateUnrealizedGainForEachHolding() {
        dsl.setEnd("2022-01-01");
        dsl.setGroupingDisabled();
        dsl.setColumns("deps,wths,div,intr,fees,ncost,rgain,ugain,val,xirr,profit");
        dsl.setReportHoldings();

        dsl.runCalculateReturns("""
                account fidelityIsa
                currency GBP
                                
                2021-01-01 deposit 90
                2021-01-01 buy 2 APPL for 40 with fee 2
                2021-01-01 dividend 1 from APPL
                2021-01-01 sell 1 APPL for 25 with fee 2
                2021-12-31 price APPL 30 GBP
                2021-12-31 interest 7
                2021-12-31 fee 1
                2021-12-31 withdraw 1
                """);

        dsl.verifyOutput("""
                account           Deps   Wths   Intr   Fees   Xirr   RGain  UGain  Div    Profit  Cost   Value
                fidelityIsa          90     -1      7     -5      0      3     10      1      20    -89    109
                fidelityIsa:APPL      0      0      0     -4      1      3     10      1      10    -20     30
                fidelityIsa:GBP      90     -1      7     -1      …      0      0      0      10    -69     79""");
    }
}

