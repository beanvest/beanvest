package beanvest.acceptance;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class BeanvestCliAcceptanceTest {
    BeanvestDsl dsl = new BeanvestDsl();

    @AfterEach
    void tearDown() {
        dsl.close();
    }

    @Test
    void shouldPrintUsageIfNoSubcommandSpecified() {
        dsl.run();

        dsl.verifyHasPrintedUsage();
        dsl.verifyHasPrintedSubcommands("export, import, journal, report");
    }

    @Test
    void shouldExitWithErrorCodeIfNoSubcommandSpecified() {
        dsl.run();

        dsl.verifyHasNotFinishedSuccessfully();
    }
}
