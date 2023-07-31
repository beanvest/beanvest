package beanvest.acceptance.returns.stats;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.Test;

public class DividendsAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void calculatesDividend() {
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

        dsl.verifyDividends("trading:X", "TOTAL", "2");
    }
}
