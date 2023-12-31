package beanvest.acceptance.report;

import beanvest.acceptance.report.dsl.ReportDsl;
import org.junit.jupiter.api.Test;

public class MultipleAccountsAcceptanceTest {
    protected final ReportDsl dsl = new ReportDsl();

    @Test
    void calculatesGainsSeparatelyForEachAccount() {
        dsl.setEnd("2023-01-01");
        dsl.setColumns("again,xirr");

        dsl.calculateReturns("""
                account pension
                currency GBP
                                
                2019-01-01 deposit and buy 1 MSFT for 500
                ---
                account isa
                currency GBP
                2019-01-01 deposit and buy 1 MSFT for 500
                                
                ---
                2023-01-01 price MSFT 1000 GBP
                """);

        dsl.verifyAccountGain("pension", "TOTAL", "500")
                .verifyXirrCumulative("pension", "TOTAL", "18.9");
        dsl.verifyAccountGain("isa", "TOTAL", "500")
                .verifyXirrCumulative("isa", "TOTAL", "18.9");
    }

    @Test
    void calculatesCombinedReturns() {
        dsl.setEnd("2023-01-01");
        dsl.setGroupingEnabled();
        dsl.setColumns("again,xirr");

        dsl.calculateReturns("""
                account pension
                currency GBP
                                
                2019-01-01 deposit and buy 1 MSFT for 100
                ---
                account isa
                currency GBP
                2019-01-01 deposit and buy 1 TSLA for 100
                                
                ---
                2023-01-01 price MSFT 110 GBP
                2023-01-01 price TSLA 150 GBP
                """);

        dsl.verifyAccountGain(".*", "TOTAL", "60")
                .verifyXirrCumulative(".*", "TOTAL", "6.78");
    }

    @Test
    void calculatesCombinedReturnsWithOneOfTheAccountsClosed() {
        dsl.setEnd("2023-01-01");
        dsl.setGroupingEnabled();
        dsl.setColumns("again,xirr");

        dsl.calculateReturns("""
                account pension
                currency GBP
                                
                2021-01-01 deposit and buy 1 MSFT for 100
                2022-01-01 sell and withdraw 1 MSFT for 101
                2022-01-01 close
                ---
                account isa
                currency GBP
                2021-01-01 deposit and buy 1 MSFT for 100
                ---
                2022-01-01 price MSFT 101 GBP
                2023-01-01 price MSFT 102 GBP
                """);

        dsl.verifyAccountGain(".*", "TOTAL", "3");
    }
}
