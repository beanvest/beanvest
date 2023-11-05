package beanvest.acceptance.report;

import beanvest.acceptance.report.dsl.ReportDsl;
import org.junit.jupiter.api.Test;

public class AccountFilteringAcceptanceTest {
    protected final ReportDsl dsl = new ReportDsl();

    @Test
    void filterAccounts() {
        dsl.setAccountFilter("tra.*");
        dsl.setColumns("deps");

        dsl.calculateReturns("""
                account saving
                currency GBP
                                
                2021-01-01 deposit 100
                ---
                account trading
                currency GBP
                                
                2021-01-01 deposit 100
                ---
                account trading2
                currency GBP
                                
                2021-01-01 deposit 100
                """);

        dsl.verifyResultsReturnedForAccount("trading");
        dsl.verifyResultsReturnedForAccount("trading2");
        dsl.verifyResultsNotReturnedForAccount("saving1");
    }

    @Test
    void totalReturnsAreForOnlyMatchingAccounts() {
        dsl.setAccountFilter("tra.*");

        dsl.calculateReturns("""
                account saving
                currency GBP
                                
                2021-01-01 deposit 100
                2021-01-01 interest 1
                ---
                account trading
                currency GBP
                                
                2021-01-01 deposit 100
                2021-01-01 interest 1
                ---
                account trading2
                currency GBP
                                
                2021-01-01 deposit 100
                2021-01-01 interest 1
                """);
        dsl.verifyResultsNotReturnedForAccount("saving");
        dsl.verifyResultsReturnedForAccount("trading");
        dsl.verifyResultsReturnedForAccount("trading2");
    }

    @Test
    void mightSkipCalculatingAllTheAccountsSeparately() {
        dsl.setGroupsOnly();

        dsl.calculateReturns("""
                account saving
                currency GBP
                                
                2021-01-01 deposit 100
                2021-01-01 interest 1
                ---
                account trading
                currency GBP
                                
                2021-01-01 deposit 100
                2021-01-01 interest 1
                """);
        dsl.verifyResultsNotReturnedForAccount("trading");
        dsl.verifyResultsNotReturnedForAccount("saving");
        dsl.verifyResultsReturnedForAccount(".*");
    }
}
