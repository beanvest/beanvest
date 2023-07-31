package beanvest.acceptance.returns.stats;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class XirrPeriodicAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();
    @Test
    void calculatesXirrForEachPeriodSeparately() {
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
    @Disabled("todo")
    void shouldCalculatePeriodicXirrForEachHolding()
    {
        dsl.setReportHoldings();
        dsl.setEnd("2024-01-01");

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

        dsl.verifyXirrCumulative("pension", "TOTAL", "7.24");
        dsl.verifyXirrCumulative("pension:MSFT", "TOTAL", "5");//around that number
        dsl.verifyXirrCumulative("pension:APPL", "TOTAL", "10");//around that number
        dsl.verifyXirrCumulative("pension", "2022", "15");
        dsl.verifyXirrCumulative("pension:MSFT", "2022", "10");
        dsl.verifyXirrCumulative("pension:APPL", "2022", "20");
        dsl.verifyXirrCumulative("pension", "2023", "0");
        dsl.verifyXirrCumulative("pension:MSFT", "2023", "0");
        dsl.verifyXirrCumulative("pension:APPL", "2023", "0");
    }
}
