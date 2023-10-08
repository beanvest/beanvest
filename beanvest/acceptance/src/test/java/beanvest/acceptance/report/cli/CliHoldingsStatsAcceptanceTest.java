package beanvest.acceptance.report.cli;

import beanvest.acceptance.report.ReportDsl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CliHoldingsStatsAcceptanceTest {
    protected final ReportDsl dsl = new ReportDsl();

    @BeforeEach
    void setUp() {
        dsl.setCliOutput();
    }

    @Test
    void shouldCalculateUnrealizedGainForEachHolding() {
        dsl.setEnd("2022-01-01");
        dsl.setGroupingDisabled();
        dsl.setColumns("deps,wths,intr,fees,xirr,rgain,ugain,div,value");
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
                Account           Deps   Wths   Intr   Fees   Xirr   RGain  UGain  Div    Value
                fidelityIsa          90     -1      7     -5   24.4      3     10      1    111
                fidelityIsa:APPL      0      0      0     -4  114.3      3     10      1     30
                fidelityIsa:GBP      90     -1      7     -1      …      0      0      0     81""");
    }
}

