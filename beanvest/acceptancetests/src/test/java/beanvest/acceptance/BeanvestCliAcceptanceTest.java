package beanvest.acceptance;

import org.junit.jupiter.api.Test;

public class BeanvestCliAcceptanceTest {
    BeanvestDsl dsl = new BeanvestDsl();

    @Test
    void shouldPrintUsageIfNoSubcommandSpecified() {
        dsl.run();

        dsl.verifyHasPrintedUsage();
        dsl.verifyHasPrintedSubcommands("export, import, journal, returns");
    }

    @Test
    void shouldExitWithErrorCodeIfNoSubcommandSpecified() {
        dsl.run();

        dsl.verifyHasNotFinishedSuccessfully();
    }
}
