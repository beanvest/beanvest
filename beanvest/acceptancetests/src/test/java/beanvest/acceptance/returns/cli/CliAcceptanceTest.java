package beanvest.acceptance.returns.cli;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CliAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @BeforeEach
    void setUp() {
        dsl.setCliOutput();
    }

    @Test
    void calculatesStatsOnWholeJournalByDefault() {
        dsl.setColumns("cash");

        dsl.runCalculateReturns(
                """
                        account taxable
                        currency GBP
                                        
                        2020-02-02 deposit 10
                        2022-02-02 deposit 10
                        """);

        dsl.verifyOutput("""
                account  cash
                taxable     20""");
    }

    @Test
    void canPrintOutputAsJson() {
        dsl.setEnd("2023-01-01");
        dsl.setJsonOutput();

        dsl.runCalculateReturns(
                """
                        account taxable
                        currency GBP
                                        
                        2021-01-01 deposit and buy 1 VLS for 100
                        2023-01-01 price VLS 121""");

        dsl.verifyOutputIsValidJson();
    }

    @Test
    void statsAreCalculatedForAllAccounts() {
        dsl.setEnd("2023-01-01");
        dsl.setColumns("cash");

        dsl.runCalculateReturns("""
                account isa
                currency GBP
                                
                2021-01-01 deposit 20
                ---
                account pension
                currency GBP
                                
                2021-01-01 deposit 15
                """);

        dsl.verifyOutput("""
                account  cash
                isa         20
                pension     15""");
    }
}

