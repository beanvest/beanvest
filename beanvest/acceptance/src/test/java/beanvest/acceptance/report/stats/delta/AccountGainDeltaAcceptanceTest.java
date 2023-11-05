package beanvest.acceptance.report.stats.delta;

import beanvest.acceptance.report.dsl.ReportDsl;
import org.junit.jupiter.api.Test;

public class AccountGainDeltaAcceptanceTest {
    protected final ReportDsl dsl = new ReportDsl();

    @Test
    void accountGainDeltaAfterOpeningOfTheAccount() {
        dsl.setEnd("2021-03-01");
        dsl.setMonthly();
        dsl.setDeltas();
        dsl.setColumns("again");

        dsl.calculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit and buy 1 VLS for 100
                                        
                2021-01-31 price VLS 110 GBP
                """
        );

        dsl.verifyAccountGainDelta("trading", "21m01", "10");
    }
    @Test
    void accountGainIncludesValueGainInConsecutivePeriods() {
        dsl.setEnd("2021-03-01");
        dsl.setMonthly();
        dsl.setColumns("again");
        dsl.setDeltas();

        dsl.calculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit and buy 1 VLS for 100
                                        
                2021-01-31 price VLS 110 GBP
                2021-02-28 price VLS 111 GBP
                """
        );

        dsl.verifyAccountGainDelta("trading", "21m02", "1");
    }

    @Test
    void accountGainIncludesInterest() {
        dsl.setEnd("2021-03-01");
        dsl.setMonthly();
        dsl.setColumns("again");
        dsl.setDeltas();

        dsl.calculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit 10
                                
                2021-02-01 interest 5
                """
        );

        dsl.verifyAccountGainDelta("trading", "21m01", "0");
        dsl.verifyAccountGainDelta("trading", "21m02", "5");
    }

    @Test
    void accountGainIncludesFees() {
        dsl.setEnd("2021-03-01");
        dsl.setMonthly();
        dsl.setColumns("again");
        dsl.setDeltas();

        dsl.calculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit 10
                2021-02-01 fee 2
                """
        );

        dsl.verifyAccountGainDelta("trading", "21m01", "0");
        dsl.verifyAccountGainDelta("trading", "21m02", "-2");
    }

    @Test
    void accountGainWithSomeRealizedGain() {
        dsl.setEnd("2021-02-01");
        dsl.setDeltas();
        dsl.setMonthly();
        dsl.setColumns("again");

        dsl.calculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit and buy 1 X for 10
                2021-01-02 sell 1 X for 12
                2021-01-03 withdraw 12
                """
        );

        dsl.verifyAccountGainDelta("trading", "21m01", "2");
    }
}
