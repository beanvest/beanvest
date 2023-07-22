package beanvest.acceptance.returns;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class PeriodDeltasAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void yearlyPeriodsContainOnlyDataFromThatPeriod() {
        dsl.setYearly();

        dsl.runCalculateReturns("""
                account savings
                currency GBP
                                
                2020-01-01 deposit 100
                2021-01-01 deposit 100
                """);

        dsl.verifyDepositsDelta("savings", "2020", "100");
        dsl.verifyDepositsDelta("savings", "2021", "100");
    }

    @Test
    @Disabled("needs some thinking")
    void firstRequestedPeriodHasOnlyDataFromItsPeriodAsWell() {
        dsl.setYearly();
        dsl.setStartDate("2021-01-01");

        dsl.runCalculateReturns("""
                account savings
                currency GBP
                                
                2020-01-01 deposit 100
                2021-01-01 deposit 100
                """);

        dsl.verifyDepositsDelta("savings", "2021", "100");
    }

    @Test
    void doesNotReportEarlierPeriodsThanStartDate() {
        dsl.setYearly();
        dsl.setStartDate("2021-01-01");

        dsl.runCalculateReturns("""
                account savings
                currency GBP
                                
                2020-01-01 deposit 100
                2021-01-01 deposit 100
                """);

        dsl.verifyHasNoStats("savings", "2020");
    }

    @Test
    void quarterlyPeriodsContainOnlyDataFromThatPeriod() {
        dsl.setQuarterly();

        dsl.runCalculateReturns("""
                account savings
                currency GBP
                                
                2020-01-01 deposit 100
                2020-03-31 deposit 200
                2020-04-30 deposit 400
                """);

        dsl.verifyDepositsDelta("savings", "20q1", "300");
        dsl.verifyDepositsDelta("savings", "20q2", "400");
    }

    @Test
    @Disabled("TODO refactor")
    void monthlyPeriodsContainOnlyDataFromThatPeriod() {
        dsl.setMonthly();

        dsl.runCalculateReturns("""
                account savings
                currency GBP
                                
                2020-01-01 deposit 100
                2020-02-15 deposit 200
                2020-03-31 deposit 400
                """);

        dsl.verifyDepositsDelta("savings", "20m01", "100");
        dsl.verifyDeposits("savings", "20m02", "200");
        dsl.verifyDeposits("savings", "20m03", "400");
    }

    @Test
    @Disabled("TODO refactor")
    void noOtherPeriodsShownExceptForMatchingOne() {
        dsl.setStartDate("2020-02-01");
        dsl.setEnd("2020-03-01");
        dsl.setMonthly();

        dsl.runCalculateReturns("""
                account savings
                currency GBP
                                
                2020-01-01 deposit 100
                2020-02-15 deposit 200
                2020-03-31 deposit 400
                """);

        dsl.verifyHasNoStats("savings", "20m01");
        dsl.verifyDepositsDelta("savings", "20m02", "200");
        dsl.verifyHasNoStats("savings", "20m03");
    }
}
