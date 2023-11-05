package beanvest.acceptance.report.stats;

import beanvest.acceptance.report.dsl.ReportDsl;
import org.junit.jupiter.api.Test;

public class DividendsAcceptanceTest {
    protected final ReportDsl dsl = new ReportDsl();

    @Test
    void calculatesDividend() {
        dsl.setColumns("Div");
        dsl.setReportHoldings();
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 100
                2022-06-05 buy 1 X for 10
                2022-07-05 dividend 2 from X
                2022-09-05 dividend 3 from X
                                
                $$TODAY$$ price X 13
                """);

        dsl.verifyDividends("trading", "TOTAL", "5");
    }

    @Test
    void calculatesDividendOfHoldings() {
        dsl.setColumns("Div");
        dsl.setReportHoldings();
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 100
                2022-06-05 buy 1 X for 10
                2022-06-05 buy 1 Y for 10
                2022-07-05 dividend 2 from X
                2022-09-05 dividend 3 from Y
                                
                $$TODAY$$ price X 13
                """);

        dsl.verifyDividends("trading", "TOTAL", "5");
        dsl.verifyDividends("trading:X", "TOTAL", "2");
        dsl.verifyDividends("trading:Y", "TOTAL", "3");
    }

    @Test
    void dividendsAddValue() {
        dsl.setColumns("value");
        dsl.setReportHoldings();
        dsl.setEnd("2023-01-01");
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 20
                2022-06-05 buy 1 X for 10
                2022-06-05 buy 1 Y for 10
                2022-07-05 dividend 2 from X
                2022-09-05 dividend 3 from Y
                2022-12-31 price X 10 GBP
                2022-12-31 price Y 10 GBP
                """);

        dsl.verifyValue("trading:GBP", "TOTAL", "5");
    }
}
