package beanvest.acceptance.returns.cli;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("rework v2")
public class CliAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @BeforeEach
    void setUp() {
        dsl.setCliOutput();
    }

    @AfterEach
    void tearDown() {
        dsl.cleanUp();
    }

    @Test
    void calculatesStatsOnWholeJournalByDefault() {
        dsl.setColumns("cash");
        dsl.setGroupingDisabled();

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
        dsl.setGroupingDisabled();
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

    @Test
    void shouldShowSpecifiedColumns() {
        dsl.setEnd("2023-01-01");
        dsl.setGroupingDisabled();
        dsl.setColumns("deps,wths");

        dsl.runCalculateReturns("""
                account isa
                currency GBP
                                
                2021-01-01 deposit 20
                2021-01-01 withdraw 10
                """);

        dsl.verifyOutput("""
                account  deps   wths
                isa         20    -10""");
    }

    @Test
    void shouldReadWholeDirectoryOfJournals() {
        dsl.setEnd("2022-01-01");
        dsl.setStartDate("2021-01-01");
        dsl.setGroupingDisabled();
        dsl.setColumns("deps");

        dsl.storeJournal("myJournals/account1.bv",
                """
                        account acc1
                        currency GBP
                                        
                        2021-01-01 deposit 20
                        """
        );
        dsl.storeJournal("myJournals/new/account2.bv",
                """
                        account acc2
                        currency GBP
                                        
                        2021-01-01 deposit 21
                        """
        );

        dsl.runCalculateReturnsOnDirectory("myJournals");

        dsl.verifyOutput("""
                account  deps
                acc1        20
                acc2        21""");
    }
}

