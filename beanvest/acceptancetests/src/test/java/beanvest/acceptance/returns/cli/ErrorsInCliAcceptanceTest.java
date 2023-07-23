package beanvest.acceptance.returns.cli;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ErrorsInCliAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @BeforeEach
    void setUp() {
        dsl.setCliOutput();
    }

    @Test
    void printsUsageIfNoArgsGiven() {
        dsl.runWithArgumentCount(0);
        dsl.verifyNonZeroExitCode()
                .verifyUsagePrinted();
    }

    @Test
    void printsErrorIfJournalCantBeRead() {
        dsl.setAllowNonZeroExitCodes();

        dsl.runCalculateReturnsWithFilesArgs("missingJournal");
        dsl.verifyNonZeroExitCode()
                .verifyStdErrContains("Journal `missingJournal` not found");
    }

    @Test
    void printsTableJustFineIfXirrIsNotCalculated() {
        dsl.setEnd("2021-03-15");
        dsl.setColumns("xirr");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit and buy 1000 VLS for 1000
                """);

        dsl.verifyOutput("""                                
                account  xirr
                trading      …""");
    }

    @Test
    void printsTripleDotIfAccountWasClosedInTheInterval() {
        dsl.setStartDate("2020-01-15");
        dsl.setEnd("2022-01-01");
        dsl.setColumns("deps");
        dsl.setDeltas();
        dsl.setYearly();

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2020-03-01 deposit 10
                2020-10-01 withdraw 10
                2020-10-01 close
                """);

        dsl.verifyOutput("""
                        ╷ 2021  ╷ 2020  ╷
                account │ Δdeps │ Δdeps │
                trading │     … │    10 │""");
    }

    @Test
    @Disabled("processing refactor")
    void calculatesDepositsAndWithdrawalsWithoutPricesNeededForXirrAndValuation() {
        dsl.setEnd("2021-03-15");
        dsl.setColumns("dw");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit and buy 1000 VLS for 1000
                """);

        dsl.verifyOutput("""
                account  dw
                trading  1,000""");
        dsl.verifyNoWarningsShown();
    }
}

