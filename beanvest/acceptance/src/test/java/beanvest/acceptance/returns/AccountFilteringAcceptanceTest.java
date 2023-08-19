package beanvest.acceptance.returns;

import org.junit.jupiter.api.Test;

public class AccountFilteringAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void filterAccounts() {
        dsl.setAccountFilter("tra.*");
        dsl.setColumns("deps");

        dsl.runCalculateReturns("""
                account saving
                currency GBPgit 
                                
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

        dsl.runCalculateReturns("""
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

        dsl.runCalculateReturns("""
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
