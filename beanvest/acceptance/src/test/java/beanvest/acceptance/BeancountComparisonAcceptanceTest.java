package beanvest.acceptance;

import beanvest.BeanvestMain;
import beanvest.acceptance.report.dsl.BeanvestRunner;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("rework v2")
public class BeancountComparisonAcceptanceTest {
    private final BeancountComparisonDsl dsl = new BeancountComparisonDsl(
            BeanvestRunner.createRunner(BeanvestMain.class, "export"),
            BeanvestRunner.createRunner(BeanvestMain.class, "returns")
    );

    @Test
    void cashOperationsHaveSameResult() {
        var journal = """
                account Assets:Savings
                currency GBP
                                
                2020-02-02 deposit 10
                2021-02-02 interest 15
                2022-02-02 withdraw 10
                """;

        dsl.runReports(journal);

        dsl.verifyCashMatchesInBeancountAndBeanvest();
    }
}

