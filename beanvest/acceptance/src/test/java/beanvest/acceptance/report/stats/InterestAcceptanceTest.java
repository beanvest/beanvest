package beanvest.acceptance.report.stats;

import beanvest.acceptance.report.dsl.ReportDsl;
import org.junit.jupiter.api.Test;

public class InterestAcceptanceTest {
    protected final ReportDsl dsl = new ReportDsl();

    @Test
    void calculatesInterestTotal() {
        dsl.setColumns("Intr");
        dsl.setReportHoldings();
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 100
                2022-06-05 interest 10
                2022-07-05 interest 2.2
                2022-06-05 interest -1
                """);

        dsl.verifyInterest("trading", "TOTAL", "11.2");
        dsl.verifyInterest("trading:GBP", "TOTAL", "11.2");
    }

    @Test
    void holdingsCantGenerateInterest() {
        dsl.setColumns("Intr");
        dsl.setReportHoldings();
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 100
                2022-06-05 buy 1 MSFT for 100
                2022-06-05 dividend 10 from MSFT
                """);

        dsl.verifyInterestError("trading:MSFT", "TOTAL", "n/a");
    }
}
