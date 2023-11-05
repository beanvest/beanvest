package beanvest.acceptance.report.stats;

import beanvest.acceptance.report.dsl.ReportDsl;
import org.junit.jupiter.api.Test;

public class OpenAndCloseDatesAcceptanceTest {
    protected final ReportDsl dsl = new ReportDsl();

    @Test
    void returnsAccountOpeningDate() {
        dsl.calculateReturns("""
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

        dsl.calculateReturns("""
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

    @Test
    void shouldReturnOpeningAndClosingDatesOfHoldings() {
        dsl.setColumns("Opened,Closed");
        dsl.setReportHoldings();
        dsl.calculateReturns("""
                account saving
                currency GBP
                                
                2020-01-01 deposit 100
                2020-01-02 buy 1 APPL for 100
                2020-01-03 sell 1 APPL for 100
                """);

        dsl.verifyAccountOpeningDate("saving:APPL", "2020-01-02");
        dsl.verifyAccountClosingDate("saving:APPL", "2020-01-03");
    }
}
