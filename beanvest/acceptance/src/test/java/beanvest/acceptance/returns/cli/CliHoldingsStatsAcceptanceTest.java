package beanvest.acceptance.returns.cli;

import beanvest.acceptance.returns.ReturnsDsl;
import beanvest.lib.testing.WorkInProgress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
        dsl.setColumns("deps,wths,intr,fees,xirr,rgain,ugain,div,profit,cost,value");
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
                Account           Deps   Wths   Intr   Fees   Xirr   RGain  UGain  Div    Profit  Cost   Value
                fidelityIsa          90     -1      7     -5   24.4      3     10      1      22    -89    111
                fidelityIsa:APPL      0      0      0     -4  114.3      3     10      1      10    -20     30
                fidelityIsa:GBP      90     -1      7     -1      …      0      0      0      12    -69     81""");
    }
}

