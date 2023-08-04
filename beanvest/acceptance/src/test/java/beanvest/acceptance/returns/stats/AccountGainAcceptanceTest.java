package beanvest.acceptance.returns.stats;

import beanvest.acceptance.returns.ReturnsDsl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("rework v2")
public class AccountGainAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @Test
    void returnsAreCalculatedForMultipleSecurities() {
        dsl.setEnd("2021-01-21");

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit and buy 1 VLS for 100
                2021-01-01 deposit and buy 1 TSLA for 100
                                        
                2021-01-21 price VLS 200 GBP
                2021-01-21 price TSLA 300 GBP
                """
        );

        dsl.verifyAccountGain("trading", "TOTAL", "300");
    }


    @Test
    void returnIsCalculatedUpToClosingDateOfTheAccount() {
        dsl.setEnd("2021-03-03");
        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit and buy 1000 VLS for 1000
                2021-03-01 sell and withdraw 1000 VLS for 1100
                2021-03-02 close
                """);

        dsl.verifyAccountGain("trading", "TOTAL", "100")
                .verifyClosingDate("trading", "2021-03-02");
    }

    @Test
    void shouldCalculateGainHoldings() {
        dsl.setEnd("2021-01-21");
        dsl.setReportHoldings();

        dsl.runCalculateReturns("""
                account trading
                currency GBP
                                
                2021-01-01 deposit and buy 2 VLS for 100
                2021-01-05 sell 1 VLS for 55
                                        
                2021-01-21 price VLS 60 GBP
                """
        );

        dsl.verifyAccountGain("trading:VLS", "TOTAL", "15");
    }
}
