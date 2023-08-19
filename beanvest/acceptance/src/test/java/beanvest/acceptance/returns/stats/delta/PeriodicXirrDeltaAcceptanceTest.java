package beanvest.acceptance.returns.stats.delta;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class PeriodicXirrDeltaAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void calculatesXirrForEachPeriodIfPeriodicDeltasRequested() {
        dsl.setYearly();
        dsl.setColumns("xirr");
        dsl.setDeltas();
        dsl.setEnd("2024-01-01");
        dsl.setGroupingDisabled();
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit and buy 10 VLS for 10
                2021-12-31 price VLS 2 GBP
                2022-12-31 price VLS 2 GBP
                2023-12-31 price VLS 3 GBP
                """);

        dsl.verifyXirrPeriodic("trading", "2021", "100");
        dsl.verifyXirrPeriodic("trading", "2022", "0");
        dsl.verifyXirrPeriodic("trading", "2023", "50");
    }

    @Test
    void calculatesAccountXirrIncludingCash() {
        dsl.setYearly();
        dsl.setColumns("xirr");
        dsl.setDeltas();
        dsl.setEnd("2022-01-01");
        dsl.setGroupingDisabled();
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit and buy 10 VLS for 10
                2021-12-31 dividend 5 from VLS
                2021-12-31 price VLS 1 GBP
                """);

        dsl.verifyXirrPeriodic("trading", "2021", "50");
    }

    @Test
    void calculatesAccountXirrIncludingFees() {
        dsl.setYearly();
        dsl.setColumns("xirr");
        dsl.setDeltas();
        dsl.setEnd("2022-01-01");
        dsl.setGroupingDisabled();

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                           
                2021-01-01 deposit 20
                2021-01-01 buy 10 VLS for 10
                2021-12-31 fee 10
                2021-12-31 price VLS 1 GBP
                """);

        dsl.verifyXirrPeriodic("trading", "2021", "-50");
    }
    @Test
    void calculatesHoldingXirr() {
        dsl.setYearly();
        dsl.setColumns("xirr");
        dsl.setDeltas();
        dsl.setEnd("2022-01-01");
        dsl.setGroupingDisabled();
        dsl.setReportHoldings();

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                           
                2021-01-01 deposit 20
                2021-01-01 buy 10 VLS for 10
                2021-12-31 fee 10
                2021-12-31 price VLS 2 GBP
                """);

        dsl.verifyXirrPeriodic("trading:VLS", "2021", "100");
    }

    @Test
    void calculatesHoldingXirrIncludingDividends() {
        dsl.setYearly();
        dsl.setColumns("xirr");
        dsl.setDeltas();
        dsl.setEnd("2022-01-01");
        dsl.setGroupingDisabled();
        dsl.setReportHoldings();

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                           
                2021-01-01 deposit and buy 10 VLS for 10
                2021-12-31 dividend 2 from VLS
                2021-12-31 price VLS 1 GBP
                """);

        dsl.verifyXirrPeriodic("trading:VLS", "2021", "20");
    }

    @Test
    void calculatesXirrForPeriodWithNoInterval() {
        dsl.setColumns("xirr");
        dsl.setDeltas();
        dsl.setEnd("2022-01-01");
        dsl.setGroupingDisabled();

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                           
                2021-01-01 deposit and buy 10 VLS for 10
                2021-12-31 dividend 2 from VLS
                2021-12-31 price VLS 1 GBP
                """);

        dsl.verifyXirrPeriodic("trading", "TOTAL", "20");
    }
}
