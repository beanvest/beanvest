package beanvest.acceptance.options;

import beanvest.BeanvestMain;
import beanvest.lib.apprunner.AppRunner;
import beanvest.lib.apprunner.AppRunnerFactory;
import beanvest.lib.apprunner.CliExecutionResult;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OptionsAcceptanceTest {
    protected AppRunner runner = AppRunnerFactory.createRunner(BeanvestMain.class, "options");

    @Test
    void shouldReturnAvailableColumns() {
        CliExecutionResult cliExecutionResult = runner.runSuccessfully(List.of());

        Gson gson = new Gson();
        String json = cliExecutionResult.stdOut();
        Options options = gson.fromJson(json, Options.class);

        Set<String> columnIds = options.columns.stream().map(c -> c.id).collect(Collectors.toSet());
        assertThat(columnIds).contains("cost", "xirr", "div", "profit");
    }

    record Options(List<Column> columns) {

        private record Column(String id) {
        }
    }
}
