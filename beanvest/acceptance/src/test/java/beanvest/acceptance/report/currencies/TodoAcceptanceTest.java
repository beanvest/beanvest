package beanvest.acceptance.report.currencies;

import beanvest.acceptance.report.dsl.ReportDsl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("well, its TODO")
public class TodoAcceptanceTest {
    protected final ReportDsl dsl = new ReportDsl();

    @Test
    void depositsAreConvertedAtTheDateWhenTheyHappen() {
        dsl.setCurrency("GBP");
        dsl.setYearly();

        dsl.calculateReturns("""
                account trading
                currency PLN
                                
                2021-01-01 deposit 50
                2021-02-01 deposit 50
                
                ---
                2021-01-01 price GBP 5 PLN
                2021-01-21 price GBP 6 PLN
                """);

        dsl.verifyDeposits("trading", "2021", "10");
        dsl.verifyDeposits("trading", "2022", "10");
    }

    @Test
    void interestIsConvertedAtTheDateWhenTheyHappen() {
        dsl.setYearly();
        dsl.setCurrency("GBP");

        dsl.calculateReturns("""
                account trading
                currency PLN
                                
                2021-01-01 interest 50
                2022-01-01 interest 60
                
                ---
                2021-01-01 price GBP 5 PLN
                2022-01-21 price GBP 6 PLN
                """);

        dsl.verifyDepositsDelta("trading", "2021", "10");
        dsl.verifyDepositsDelta("trading", "2022", "10");
    }

    @Test
    void realizedGainsCanBeConverted() {
        dsl.setCurrency("GBP");

        dsl.calculateReturns("""
                account trading
                currency PLN
                                
                2021-01-01 deposit and buy 1 X for 10
                2021-01-21 sell 1 X for 15
                
                ---
                2021-01-01 price PLN 0.20 GBP
                2021-01-21 price PLN 0.20 GBP
                """);

        dsl.verifyRealizedGains("trading", "TOTAL", "1");
    }

    @Test
    void unrealizedGainsCanBeConverted() {
        dsl.setEnd("2021-01-22");
        dsl.setCurrency("GBP");

        dsl.calculateReturns("""
                account trading
                currency PLN
                                
                2021-01-01 deposit and buy 1 X for 10
                
                ---
                2021-01-01 price PLN 0.20 GBP
                2021-01-21 price X 3 GBP
                2021-01-21 price PLN 0.20 GBP
                """);

        dsl.verifyUnrealizedGains("trading", "TOTAL", "1");
    }

    @Test
    void accountGainCanBeConverter() {
        dsl.setEnd("2021-01-22");
        dsl.setCurrency("GBP");

        dsl.calculateReturns("""
                account trading
                currency PLN
                                
                2021-01-01 deposit 40
                2021-01-01 buy 2 X for 20
                2021-01-01 sell 1 X for 15 "+1"
                2021-01-01 withdraw 10 "no change"
                ---
                2021-01-01 price PLN 0.20 GBP
                2021-01-21 price X 3 GBP "+1"
                2021-01-21 price PLN 0.20 GBP
                """);

        dsl.verifyProfit("trading", "TOTAL", "2");
    }

    @Test
    void currencyValueChangeIsAffectingResults() {
        dsl.setEnd("2021-01-22");
        dsl.setCurrency("GBP");

        dsl.calculateReturns("""
                account trading
                currency QWE
                                
                2021-01-01 deposit 1
                2021-01-02 withdraw 1
                ---
                2021-01-01 price QWE 2 GBP
                2021-01-02 price QWE 3 GBP
                """);

        dsl.verifyProfit("trading", "TOTAL", "1");
    }

    @Test
    void irrelevantPricesAreAllowedAnywhere() {
        dsl.calculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit and buy 1 VLS for 100
                                        
                2021-01-21 price VLS 300 PLN
                2021-01-21 price VLS 200 GBP
                2021-01-21 price VLS 300 USD
                """);

        dsl.verifyProfit("trading", "TOTAL", "100");
    }
}