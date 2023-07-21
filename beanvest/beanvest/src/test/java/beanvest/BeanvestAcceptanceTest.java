package beanvest;

import bb.lib.testing.AppRunner;
import bb.lib.testing.apprunner.DirectRunner;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
