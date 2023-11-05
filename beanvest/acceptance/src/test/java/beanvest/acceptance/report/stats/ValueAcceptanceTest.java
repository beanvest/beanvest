package beanvest.acceptance.report.stats;

import beanvest.acceptance.report.dsl.ReportDsl;
import org.junit.jupiter.api.Test;

public class ValueAcceptanceTest {
    protected final ReportDsl dsl = new ReportDsl();

    @Test
    void calculatesAccountValue() {
        dsl.setEnd("2023-01-01");
        dsl.setColumns("value");
        dsl.setYearly();
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 1
                2021-01-03 buy 1 X for 1
                2021-12-31 price X 3
                2022-12-31 price X 4
                """);

        dsl.verifyValue("trading", "2021", "3");
        dsl.verifyValue("trading", "2022", "4");
    }

    @Test
    void calculatesCashValueFromDepositsAndWithdrawals() {
        dsl.setEnd("2023-01-01");
        dsl.setColumns("value");
        dsl.setYearly();
        dsl.setReportHoldings();
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 3
                2021-01-02 withdraw 2
                """);

        dsl.verifyValue("trading:GBP", "2021", "1");
        dsl.verifyValue("trading", "2021", "1");
    }

    @Test
    void calculatesCashValueFromBuysAndSells() {
        dsl.setEnd("2023-01-01");
        dsl.setColumns("value");
        dsl.setYearly();
        dsl.setReportHoldings();
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 3
                2021-01-02 buy 1 X for 2
                2021-01-02 sell 1 X for 4
                """);

        dsl.verifyValue("trading:GBP", "2021", "5");
        dsl.verifyValue("trading", "2021", "5");
    }

    @Test
    void calculatesCashValueFromBuysAndSellsWithFeesAndRealizedGains() {
        dsl.setEnd("2023-01-01");
        dsl.setColumns("value");
        dsl.setYearly();
        dsl.setReportHoldings();
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 2
                2021-01-02 buy 1 X for 2 with fee 1
                2021-01-02 sell 1 X for 4 with fee 1
                """);

        dsl.verifyValue("trading:GBP", "2021", "4");
        dsl.verifyValue("trading", "2021", "4");
    }

    @Test
    void calculatesCashValueFromInterest() {
        dsl.setEnd("2023-01-01");
        dsl.setColumns("value");
        dsl.setYearly();
        dsl.setReportHoldings();
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 3
                2021-01-03 interest 2
                """);

        dsl.verifyValue("trading:GBP", "2021", "5");
        dsl.verifyValue("trading", "2021", "5");
    }

    @Test
    void calculatesHoldingValue() {
        dsl.setReportHoldings();
        dsl.setColumns("value");
        dsl.setEnd("2023-01-01");
        dsl.setYearly();
        dsl.setGroupingDisabled();
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 1
                2021-01-03 buy 2 X for 2
                2021-12-31 price X 2
                2022-12-31 price X 3
                """);

        dsl.verifyValue("trading:X", "2021", "4");
        dsl.verifyValue("trading:X", "2022", "6");
    }

    @Test
    void calculatesHoldingValueOfMultipleAccounts() {
        dsl.setReportHoldings();
        dsl.setColumns("value");
        dsl.setEnd("2022-01-01");
        dsl.setYearly();
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 2
                2021-01-03 buy 2 X for 2
                2021-12-31 price X 2
                ---
                account trading2
                currency GBP
                                
                2021-01-02 deposit 2
                2021-01-03 buy 2 X for 2
                2021-12-31 price X 2
                """);

        dsl.verifyValue("trading", "2021", "4");
        dsl.verifyValue("trading2", "2021", "4");
    }


    @Test
    void cashWhenSellingIsAfterFee() {
        dsl.setColumns("value");
        dsl.setReportHoldings();

        dsl.runCalculateReturns("""
                account account
                currency GBP
                    
                2017-04-10 deposit 500

                2017-08-15 buy 1 X for 500 with fee 1.50
                2017-08-16 sell 1 X for 507.82 with fee 1.50
                            
                """);

        dsl.verifyValue("account:GBP", "TOTAL", "507.82");
    }
}
