package beanvest.acceptance.returns;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class StartAndEndDateAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void usesCurrentDateByDefault() {
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit and buy 1 VLS for 100
                $$TODAY$$ price VLS 120
                """);

        dsl.verifyGainIsPositive("trading")
                .verifyEndDateIsToday();
    }

    @Test
    void endDateForReturnsCalculationCanBeProvided() {
        dsl.setEnd("2022-05-01");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit and buy 1 VLS for 100
                2022-05-01 price VLS 80 GBP
                2022-05-02 price VLS 100 GBP
                """);

        dsl.verifyAccountGain("trading", "TOTAL", "-20");
    }

    @Test
    void startDateForStatsCalculationCanBeProvided() {
        dsl.setStartDate("2021-03-01");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit and buy 1 VLS for 100
                2021-02-28 price VLS 80 GBP
                $$TODAY$$  price VLS 100 GBP
                """);

        dsl.verifyAccountGainDelta("trading", "TOTAL", "20");
    }

    @Test
    void accountsClosedAfterStartDateAreVisibleWhenRetrievingDeltas() {
        dsl.setStartDate("2021-02-01");
        dsl.setDeltas();

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit 100
                2021-02-02 withdraw 100
                2021-02-03 close
                ---
                account investing
                currency GBP
                                
                2021-05-01 deposit 100
                ---
                """);

        dsl.verifyHasStats("investing");
        dsl.verifyHasStats("trading");
    }

    @Test
    void accountsOpenAfterEndDateAreNotVisible() {
        dsl.setEnd("2016-06-01");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit 100
                2021-02-01 withdraw 100
                2021-02-03 close
                ---
                account investing
                currency GBP
                                
                2015-02-02 deposit 100
                ---
                """);

        dsl.verifyHasStats("investing");
        dsl.verifyHasNoStats("trading");
    }
}
