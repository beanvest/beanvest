package beanvest.acceptance.returns.cli;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HoldingsStatsCliAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @BeforeEach
    void setUp() {
        dsl.setCliOutput();
    }

    @Test
    void shouldCalculateUnrealizedGainForEachHolding() {
        dsl.setEnd("2022-01-01");
        dsl.setGroupingDisabled();
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
                """);

        dsl.verifyOutput("""
                account           opened      closed  deps   wths   div    intr   fees   rGain  cash   uGain  hVal   aGain  xirr   xirrp
                fidelityIsa       2021-01-01  -          90      0      1      0     -6      9     50     -2     48      8    8.9    8.9
                fidelityIsa:APPL  2021-01-01  -           0      0      0      0     -4      9      -      0      0      9      …      …
                fidelityIsa:MSFT  2021-01-01  -           0      0      1      0     -2      0      -     -2     48     -1      …      …""");
    }
}

