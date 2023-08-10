package beanvest.acceptance.returns.cli;

import beanvest.acceptance.returns.ReturnsDsl;
import beanvest.lib.testing.WorkInProgress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class HoldingsStatsCliAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @BeforeEach
    void setUp() {
        dsl.setCliOutput();
    }

    @Test
    @WorkInProgress(description = "profit,opened,closed are missing, xirr shouldnt be zero; precise review needed")
    void shouldCalculateUnrealizedGainForEachHolding() {
        dsl.setEnd("2022-01-01");
        dsl.setGroupingDisabled();
        dsl.setColumns("deps,wths,div,intr,fees,ncost,rgain,ugain,val,xirr"); //TODO add profit
        dsl.setReportHoldings();

        dsl.runCalculateReturns("""
                account fidelityIsa
                currency GBP
                                
                2021-01-01 deposit 90
                2021-01-01 buy 1 APPL for 40 with fee 2
                2021-01-01 buy 1 MSFT for 50 with fee 2
                2021-01-01 dividend 1 from MSFT
                2021-01-01 sell 1 APPL for 49 with fee 2
                2021-12-31 price APPL 45 GBP
                2021-12-31 price MSFT 48 GBP
                2021-12-31 fee 1
                2021-12-31 withdraw 1
                """);

        dsl.verifyOutput("""
                account           Deps   Wths   Intr   Fees   Xirr   RGain  UGain  Div    Cost   Value
                fidelityIsa          90     -1      0     -7      0      9     -2      1     89     94
                fidelityIsa:APPL      0      0      0     -4      …      9      0      0      0      0
                fidelityIsa:GBP      90     -1      0     -1      …      0      0      0     39     46
                fidelityIsa:MSFT      0      0      0     -2     -0      0     -2      1     50     48""");
    }
}

