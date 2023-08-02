package beanvest.acceptance.returns.stats;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class XirrPeriodicAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();
    @Test
    void calculatesXirrForAccountForEachPeriodSeparately() {
        dsl.setStartDate("2020-01-01");
        dsl.setEnd("2022-01-01");
        dsl.setYearly();

        dsl.runCalculateReturns("""
                account savings
                currency GBP
                            
                2020-01-01 deposit 100
                2020-12-31 interest 100
                2021-12-31 interest 100
                """);

        dsl.verifyXirrPeriodic("savings", "2020", "100");
        dsl.verifyXirrPeriodic("savings", "2021", "50");
    }

    @Test
    void calculatesXirrForHoldingForEachPeriodSeparately() {
        dsl.setStartDate("2020-01-01");
        dsl.setEnd("2022-01-01");
        dsl.setYearly();
        dsl.setGroupingDisabled();
        dsl.setReportHoldings();

        dsl.runCalculateReturns("""
                account savings
                currency GBP
                            
                2020-01-01 deposit and buy 1 MSFT for 100
                2020-12-31 price MSFT 200
                2021-12-31 price MSFT 300
                """);

        dsl.verifyXirrPeriodic("savings:MSFT", "2020", "100");
        dsl.verifyXirrPeriodic("savings:MSFT", "2021", "50");
    }

    @Test
    void shouldCalculatesXirrIncludingAllDividendsPaidOut() {
        dsl.setStartDate("2020-01-01");
        dsl.setEnd("2022-01-01");
        dsl.setYearly();
        dsl.setGroupingDisabled();
        dsl.setReportHoldings();

        dsl.runCalculateReturns("""
                account savings
                currency GBP
                            
                2020-01-01 deposit and buy 1 MSFT for 100
                2020-12-31 dividend 5 from MSFT
                2020-12-31 price MSFT 100
                2021-12-31 dividend 10 from MSFT
                2021-12-31 price MSFT 100
                """);

        dsl.verifyXirrPeriodic("savings:MSFT", "2020", "5");
        dsl.verifyXirrPeriodic("savings:MSFT", "2021", "10");
    }

    @Test
    @Disabled
    void shouldCalculatePeriodicXirrForEachHolding()
    {
        dsl.setReportHoldings();
        dsl.setEnd("2024-01-01");
        dsl.setYearly();

        dsl.runCalculateReturns("""
                account pension
                currency GBP
                                
                2022-01-01 deposit and buy 1 MSFT for 500
                2022-01-01 deposit and buy 1 APPL for 500
                2022-12-31 price MSFT 550 GBP
                2022-12-31 price APPL 600 GBP
                2023-12-31 price MSFT 550 GBP
                2023-12-31 price APPL 600 GBP
                """);

        dsl.verifyXirrCumulative("pension", "2022", "15");
        dsl.verifyXirrCumulative("pension:MSFT", "2022", "10");
        dsl.verifyXirrCumulative("pension:APPL", "2022", "20");
        dsl.verifyXirrCumulative("pension", "2023", "0");
        dsl.verifyXirrCumulative("pension:MSFT", "2023", "0");
        dsl.verifyXirrCumulative("pension:APPL", "2023", "0");
    }
}
