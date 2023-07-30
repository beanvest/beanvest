package beanvest.acceptance.returns.cli;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InvestmentStatsAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @BeforeEach
    void setUp() {
        dsl.setCliOutput();
    }

    @Test
    void shouldCalculateUnrealizedGainForEachHolding() {
        dsl.setEnd("2022-01-01");
        dsl.setGroupingDisabled();
        dsl.setReportInvestment();
        dsl.setColumns("ugain");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit 9
                2021-01-01 buy 1 APPL for 4
                2021-01-01 buy 1 MSFT for 5
                2021-12-31 price APPL 6 GBP
                2021-12-31 price MSFT 4 GBP
                """);

        dsl.verifyOutput("""
                account       uGain
                trading           1
                trading:APPL      2
                trading:MSFT     -1""");
    }
}

