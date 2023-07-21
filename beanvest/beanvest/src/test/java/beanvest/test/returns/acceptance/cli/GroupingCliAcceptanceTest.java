package beanvest.test.returns.acceptance.cli;

import beanvest.test.returns.ReturnsDsl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GroupingCliAcceptanceTest {
    protected final ReturnsDsl dsl = new ReturnsDsl();

    @BeforeEach
    void setUp() {
        dsl.setCliOutput();
    }

    @Test
    void calculatesInYearlyIntervalsPeriodic() {
        dsl.setEnd("2022-01-01");
        dsl.setGroup();
        dsl.setColumns("deps");

        dsl.runCalculateReturns("""
                account pension:a
                currency GBP
                                
                2021-01-01 deposit 1
                ---
                account pension:b
                currency GBP
                                
                2021-01-01 deposit 2
                ---
                account savings
                currency GBP
                                
                2021-01-01 deposit 4
                """);

        dsl.verifyOutput("""
                account     deps
                .*              7
                pension:.*      3
                pension:a       1
                pension:b       2
                savings         4""");
    }
}

