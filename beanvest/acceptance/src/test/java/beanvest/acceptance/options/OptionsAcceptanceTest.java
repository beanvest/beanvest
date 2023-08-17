package beanvest.acceptance.options;

import beanvest.BeanvestMain;
import beanvest.lib.apprunner.AppRunner;
import beanvest.lib.apprunner.AppRunnerFactory;
import beanvest.lib.apprunner.CliExecutionResult;
import beanvest.lib.apprunner.ReflectionRunner;
import com.google.gson.Gson;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OptionsAcceptanceTest {
    protected AppRunner runner = new ReflectionRunner(BeanvestMain.class, Optional.of("options")); //FIXME make it work on graal

    @Test
    void shouldReturnAvailableColumns() {
        var cliExecutionResult = runner.runSuccessfully(List.of());

        var gson = new Gson();
        var json = cliExecutionResult.stdOut();
        var options = gson.fromJson(json, Options.class);

        var columnIds = options.columns.stream().map(c -> c.id).collect(Collectors.toSet());
        assertThat(columnIds).contains("cost", "xirr", "div", "profit");
    }

    @Test
    void shouldReturnAllIntervals() {
        var cliExecutionResult = runner.runSuccessfully(List.of());

        var gson = new Gson();
        var json = cliExecutionResult.stdOut();
        var options = gson.fromJson(json, Options.class);

        var columnIds = options.intervals.stream()
                .map(v -> v.toLowerCase(Locale.ROOT)) //TODO maybe it should already be returned lower-cased
                .collect(Collectors.toSet());
        assertThat(columnIds).contains("none")
                .contains("month")
                .contains("quarter")
                .contains("year");
    }

    public record Options(List<Column> columns, List<String> intervals) {

    }

    public record Column(String id) {
    }
}
