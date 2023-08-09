package beanvest.acceptance.returns;

import org.junit.jupiter.api.Test;

public class NetCostAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void netCostOfAccount_isDepositsMinusWithdrawals() {
        dsl.setEnd("2022-01-01");
        dsl.setColumns("NCost");
        dsl.runCalculateReturns("""
                account isa
                currency GBP
                                
                2021-01-01 deposit 100
                2021-01-01 withdraw 20
                """);

        dsl.verifyCost("isa", "TOTAL", "80");
    }

    @Test
    void netCostOfHolding_shouldBeMoneySpentMinusMoneyEarnedWhenSelling() {
        dsl.setEnd("2022-01-01");
        dsl.setColumns("NCost");
        dsl.setReportHoldings();

        dsl.runCalculateReturns("""
                account isa
                currency GBP
                                
                2021-01-01 deposit 100
                2021-01-01 buy 2 APPL for 50
                2021-01-01 sell 1 APPL for 30
                2021-01-01 dividend 3 from APPL
                """);

        dsl.verifyCost("isa:APPL", "TOTAL", "25");
    }

    @Test
    void netCostOfCash_isDepositsAndSellRevenue_minusWithdrawalsAndSpentMoney() {
        dsl.setEnd("2022-01-01");
        dsl.setColumns("NCost");
        dsl.setReportHoldings();

        dsl.runCalculateReturns("""
                account isa
                currency GBP
                                
                2021-01-01 deposit 100
                2021-01-01 buy 1 APPL for 10
                2021-01-01 sell 1 APPL for 12
                2021-01-01 withdraw 7
                """);

        dsl.verifyCost("isa", "TOTAL", "93");
        dsl.verifyCost("isa:CashGBP", "TOTAL", "93");
        dsl.verifyCost("isa:APPL", "TOTAL", "0");
    }
}
