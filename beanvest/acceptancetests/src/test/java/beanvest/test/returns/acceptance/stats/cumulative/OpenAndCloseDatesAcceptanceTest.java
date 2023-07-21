package beanvest.test.returns.acceptance.stats.cumulative;

import beanvest.test.returns.ReturnsDsl;
import org.junit.jupiter.api.Test;

public class OpenAndCloseDatesAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void returnsAccountOpeningDate() {
        dsl.runCalculateReturns("""
                account saving
                currency GBP
                                
                2020-01-01 deposit 100
                ---
                account trading
                currency GBP
                                
                2021-01-01 deposit 100
                """);

        dsl.verifyAccountOpeningDate("saving", "2020-01-01");
        dsl.verifyAccountOpeningDate("trading", "2021-01-01");
    }

    @Test
    void returnsAccountClosingDate() {
        dsl.setEnd("2023-01-01");

        dsl.runCalculateReturns("""
                account saving
                currency GBP
                                
                2020-01-01 deposit 100
                2020-02-03 withdraw 100
                2020-02-03 close
                ---
                account trading
                currency GBP
                                
                2021-01-01 deposit 100
                2021-02-03 withdraw 100
                2022-03-03 close
                ---
                account fun
                currency GBP
                                
                2021-01-01 deposit 100
                """);

        dsl.verifyClosingDate("saving", "2020-02-03");
        dsl.verifyClosingDate("trading", "2022-03-03");
        dsl.verifyAccountClosingDate("fun", null);
    }

}
