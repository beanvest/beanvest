package beanvest.acceptance.returns.stats;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("rework v2")
public class XirrCumulativeAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void perAccountCalculationUsesAccountOpeningDateAsAStartDate() {
        dsl.setEnd("2023-01-01");

        dsl.runCalculateReturns("""
                account pension
                currency GBP
                                
                2022-01-01 deposit and buy 1 MSFT for 500
                2022-12-31 price MSFT 550 GBP
                """);

        dsl.verifyXirrCumulative("pension", "TOTAL", "10");
    }

    @Test
    void calculationsAreDoneForAllAccountsSeparately() {
        dsl.setEnd("2023-01-01");

        dsl.runCalculateReturns("""
                account pension
                currency GBP
                                
                2022-01-01 deposit and buy 1 MSFT for 500
                2023-01-01 price MSFT 550 GBP
                """);

        dsl.verifyXirrCumulative("pension", "TOTAL", "10");
    }

    @Test
    void calculatesXirrSeparatelyForEachAccount() {
        dsl.setEnd("2021-12-31");

        dsl.runCalculateReturns("""
                account smallerGain
                currency GBP
                                
                2021-01-01 deposit and buy 1 MSFT for 500
                ---
                account biggerGain
                currency GBP
                                
                2021-01-01 deposit and buy 1 MSFT for 550
                ---
                2021-12-31 price MSFT 550 GBP
                """);

        dsl.verifyXirrCumulative("smallerGain", "TOTAL", "10");
        dsl.verifyXirrCumulative("biggerGain", "TOTAL", "0");
    }

    @Test
    void calculatesXirrCumulativelyPeriodically() {
        dsl.setStartDate("2020-01-01");
        dsl.setEnd("2022-01-01");
        dsl.setYearly();

        dsl.runCalculateReturns("""
                account savings
                currency GBP
                            
                2020-01-01 deposit 1000
                2020-05-01 interest 20
                2021-12-15 withdraw 2020
                """);

        dsl.verifyXirrCumulative("savings", "2020", "2");
        dsl.verifyXirrCumulative("savings", "2021", "1"); //averaged down after year of 0 interest
    }

    @Test
    void cumulativeXirrUsesWholeHistoryOfTheAccount() {
        dsl.setStartDate("2020-01-01");
        dsl.setEnd("2022-01-01");
        dsl.setYearly();

        dsl.runCalculateReturns("""
                account savings
                currency GBP

                2019-03-26 deposit 200
                2020-02-01 withdraw 100
                2021-12-30 withdraw 100""");

        dsl.verifyXirrCumulative("savings", "2021", "0");
    }

    @Test
    void calculatesXirr() {
        dsl.setEnd("2023-01-01");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit and buy 1000 VLS for 1000
                2023-01-01 price VLS 1.20 GBP
                """);

        dsl.verifyXirrCumulative("trading", "TOTAL", "9.5");
    }

    @Test
    void valueFromBeforeTheStartIsUsedToCalculateXirr() {
        dsl.setStartDate("2019-01-01");
        dsl.setEnd("2020-01-01");
        dsl.runCalculateReturns("""
                account savings
                currency GBP
                                
                2018-01-01 deposit 1000
                2019-01-05 deposit 1000
                """);

        dsl.verifyXirrCumulative("savings", "TOTAL", "0");
    }

    @Test
    void calculatesCumulativeXirrForEveryPeriod() {
        dsl.setYearly();
        dsl.setEnd("2023-01-01");
        dsl.runCalculateReturns("""
                account savings
                currency GBP
                                     
                2021-01-01 deposit and buy 10 VLS for 10
                2021-12-31 price VLS 1.14 GBP
                2022-12-31 price VLS 1.00 GBP
                """);

        dsl.verifyXirrCumulative("savings", "2021", "14");
        dsl.verifyXirrCumulative("savings", "2022", "0");
    }

    @Test
    void xirrOnLeveragedReturns() {
        dsl.setYearly();
        dsl.setGroupingEnabled();
        dsl.setEnd("2023-01-01");
        dsl.runCalculateReturns("""
                account savings
                currency GBP
                                     
                2022-01-01 deposit 110
                2022-12-31 interest 20
                ---
                account loan
                currency GBP
                                     
                2022-01-01 withdraw 100
                """);

        dsl.verifyXirrCumulative(".*", "2022", "200.9");
    }

    @Test
    void xirrOnLeveragedLoss() {
        dsl.setYearly();
        dsl.setGroupingEnabled();
        dsl.setEnd("2023-01-01");
        dsl.runCalculateReturns("""
                account savings
                currency GBP
                                     
                2022-01-01 deposit 200
                2022-12-31 interest -50
                ---
                account loan
                currency GBP
                                    
                2022-01-01 withdraw 100
                """);

        dsl.verifyXirrCumulative("savings", "2022", "-25.1"); // lost 50 out of invested 200
        dsl.verifyXirrCumulative(".*", "2022", "-50.1"); // lost 50 out of invested 100
    }

    @Test
    void xirrOnLoanWithLoss() {
        dsl.setYearly();
        dsl.setGroupingEnabled();
        dsl.setEnd("2023-01-01");
        dsl.runCalculateReturns("""
                account savings
                currency GBP
                                     
                2022-01-01 deposit 50
                2022-12-31 interest -25
                ---
                account loan
                currency GBP
                                    
                2022-01-01 withdraw 100
                """);

        dsl.verifyXirrCumulative("savings", "2022", "-50.1");
        dsl.verifyXirrCumulative(".*", "2022", "50.2"); // -50 is not used here. -25 is half of -50.
    }

    @Test
    @Disabled("TODO")
    void shouldCalculateXirrForEachHolding() {
        dsl.setReportHoldings();
        dsl.setEnd("2023-01-01");

        dsl.runCalculateReturns("""
                account pension
                currency GBP
                                
                2022-01-01 deposit and buy 1 MSFT for 500
                2022-01-01 deposit and buy 1 APPL for 500
                2022-12-31 price MSFT 550 GBP
                2022-12-31 price APPL 600 GBP
                """);

        dsl.verifyXirrCumulative("pension", "TOTAL", "15");
        dsl.verifyXirrCumulative("pension:MSFT", "TOTAL", "10");
        dsl.verifyXirrCumulative("pension:APPL", "TOTAL", "20");
    }
}
