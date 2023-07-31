package beanvest.acceptance.returns.stats;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class CashAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void feesAreSubtracted() {
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 50
                2021-01-02 fee 2
                """);

        dsl.verifyCash("trading", "TOTAL", "48");
    }

    @Test
    void purchasesAreSubtracted() {
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 50
                2021-01-02 buy 1 X for 30
                $$TODAY$$ price X 30
                """);

        dsl.verifyCash("trading", "TOTAL", "20");
    }

    @Test
    void salesAreAddingToCashBalance() {
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 50
                2021-01-03 buy 1 X for 30
                2021-01-04 sell 1 X for 35
                """);

        dsl.verifyCash("trading", "TOTAL", "55");
    }

    @Test
    void transactionFeesAreDeductedFromTotalPriceAndDoNotAffectCashBalance() {
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 50
                2021-01-03 buy 1 X for 30 with fee 0.5
                2021-01-04 sell 1 X for 35 with fee 0.1
                """);

        dsl.verifyCash("trading", "TOTAL", "55");
    }

    @Test
    void interestIsIncludedInCashBalance() {
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 50
                2021-02-02 interest 10
                2021-02-02 interest -5
                """);

        dsl.verifyCash("trading", "TOTAL", "55");
    }

    @Test
    void withdrawalsAreSubtracted() {
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 50
                2021-02-02 withdraw 10
                """);

        dsl.verifyCash("trading", "TOTAL", "40");
    }

    @Test
    void dividendsAreAdded() {
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 50
                2021-01-03 buy 1 X for 30
                2021-02-02 dividend 5 from X
                $$TODAY$$ price X 30
                """);

        dsl.verifyCash("trading", "TOTAL", "25");
    }

    @Test
    void cashStatIsNotAvailableForHoldings() {
        dsl.setReportHoldings();
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-02 deposit 50
                2021-01-03 buy 1 X for 30
                2021-02-02 dividend 5 from X
                $$TODAY$$ price X 30
                """);

        dsl.verifyCashError("trading:X", "TOTAL", "DISABLED_FOR_ACCOUNT_TYPE");
    }
}
