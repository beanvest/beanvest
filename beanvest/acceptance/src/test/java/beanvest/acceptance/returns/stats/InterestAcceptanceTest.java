package beanvest.acceptance.returns.stats;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class InterestAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void calculatesInterestTotal() {
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 100
                2022-06-05 interest 10
                2022-07-05 interest 2.2
                2022-06-05 interest -1
                """);

        dsl.verifyInterest("trading", "TOTAL", "11.2");
    }

    @Test
    @Disabled("required Result in CashStat")
    void holdingsCantGenerateInterest() {
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