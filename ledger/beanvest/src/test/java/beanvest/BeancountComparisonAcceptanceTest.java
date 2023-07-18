package beanvest;

import bb.lib.testing.AppRunnerFactory;
import com.opencsv.exceptions.CsvException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@Disabled("FIX ME")
public class BeancountComparisonAcceptanceTest {
    private final BeancountComparisonDsl dsl = new BeancountComparisonDsl(
            AppRunnerFactory.createRunner(BeanvestMain.class, "export"),
            AppRunnerFactory.createRunner(BeanvestMain.class, "returns")
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

