package beanvest.test;

import beanvest.BeanvestMain;
import beanvest.lib.testing.AppRunner;
import beanvest.lib.testing.apprunner.DirectRunner;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BeanvestAcceptanceTest {
    protected AppRunner runner = new DirectRunner(BeanvestMain.class);

    @Test
    @Disabled("TODO")
    void simpleCase() {
        var cliResult = runner.run(List.of());

        assertThat(cliResult.exitCode()).isEqualTo(0);
        assertThat(cliResult.stdErr()).isEmpty();
        assertThat(cliResult.stdOut())
                .contains("Usage")
                .contains("returns")
                .contains("export")
        ;
    }
}
