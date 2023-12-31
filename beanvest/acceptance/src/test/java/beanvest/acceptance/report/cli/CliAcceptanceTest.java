package beanvest.acceptance.report.cli;

import beanvest.acceptance.report.dsl.ReportDsl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CliAcceptanceTest {
    protected final ReportDsl dsl = new ReportDsl();

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
        dsl.setColumns("value");
        dsl.setGroupingDisabled();

        dsl.calculateReturns(
                """
                        account taxable
                        currency GBP
                                        
                        2020-02-02 deposit 10
                        2022-02-02 deposit 10
                        """);

        dsl.verifyOutput("""
                Account  Value
                taxable     20""");
    }

    @Test
    void canPrintOutputAsJson() {
        dsl.setEnd("2023-01-01");
        dsl.setJsonOutput();

        dsl.calculateReturns(
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
        dsl.setColumns("value");

        dsl.calculateReturns("""
                account isa
                currency GBP
                                
                2021-01-01 deposit 20
                ---
                account pension
                currency GBP
                                
                2021-01-01 deposit 15
                """);

        dsl.verifyOutput("""
                Account  Value
                isa         20
                pension     15""");
    }

    @Test
    void shouldShowSpecifiedColumns() {
        dsl.setEnd("2023-01-01");
        dsl.setGroupingDisabled();
        dsl.setColumns("Deps,Wths");

        dsl.calculateReturns("""
                account isa
                currency GBP
                                
                2021-01-01 deposit 20
                2021-01-01 withdraw 10
                """);

        dsl.verifyOutput("""
                Account  Deps   Wths
                isa         20    -10""");
    }

    @Test
    void shouldReadWholeDirectoryOfJournals() {
        dsl.setEnd("2022-01-01");
        dsl.setStartDate("2021-01-01");
        dsl.setGroupingDisabled();
        dsl.setColumns("Deps");

        dsl.storeJournal("myJournals/account1.bv",
                """
                        account acc1
                        currency GBP
                                        
                        2021-01-01 deposit 20
                        """
        );
        dsl.storeJournal("myJournals/new/account.bv",
                """
                        account acc2
                        currency GBP
                                        
                        2021-01-01 deposit 21
                        """
        );

        dsl.calculateReturnsForDirectory("myJournals");

        dsl.verifyOutput("""
                Account  Deps
                acc1        20
                acc2        21""");
    }
}

