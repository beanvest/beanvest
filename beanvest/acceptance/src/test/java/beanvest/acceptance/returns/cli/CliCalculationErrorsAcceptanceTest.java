package beanvest.acceptance.returns.cli;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class CliCalculationErrorsAcceptanceTest {
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
        dsl.setGroupingDisabled();

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit and buy 1000 VLS for 1000
                """);

        dsl.verifyOutput("""                                
                Account  Xirr
                trading     PN""");
    }

    @Test
    void showsWarningIfHoldingHasNoPricesAtAll() {
        dsl.setEnd("2022-02-01");
        dsl.setColumns("ugain");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2020-01-01 deposit and buy 1 VLS for 1
                """);
        dsl.verifyReturnedAnError("No price set for VLS/GBP before or on 2022-02-01");
    }

    @Test
    void showsWarningIfLastKnownPriceIsTooOld() {
        dsl.setEnd("2022-02-01");
        dsl.setColumns("ugain");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2020-01-01 deposit and buy 1 VLS for 1
                2020-01-02 price VLS 2 GBP
                """);
        dsl.verifyReturnedAnError("Price gap is too big for VLS/GBP on 2022-02-01. Last price is 2 GBP from 2020-01-02");
    }

    @Test
    void printsTripleDotIfAccountWasClosedInTheInterval() {
        dsl.setStartDate("2020-01-15");
        dsl.setEnd("2022-01-01");
        dsl.setColumns("deps");
        dsl.setGroupingDisabled();
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
                Account │ pDeps │ pDeps │
                trading │     … │    10 │""");
    }

    @Test
    void calculatesCashStatsJustFineWithoutPricesNeededForValueStats() {
        dsl.setEnd("2021-03-15");
        dsl.setColumns("deps");
        dsl.setGroupingDisabled();

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit and buy 1000 VLS for 1000
                """);

        dsl.verifyOutput("""
                Account  Deps
                trading  1,000""");
        dsl.verifyNoWarningsShown();
    }
}

