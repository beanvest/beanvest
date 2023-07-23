package beanvest.module.returns;

import beanvest.processor.calendar.PeriodInterval;
import picocli.CommandLine;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ReturnsCliParametersParser {

    public ReturnsAppParameters retrieveCliParameters(CommandLine.ParseResult parseResult) {
        var journalsPaths = parseResult.matchedPositionalValue(0, new ArrayList<Path>());
        final Optional<LocalDate> overrideToday = Optional.ofNullable(parseResult.matchedOptionValue("--override-today", null));
        var today = overrideToday.orElseGet(LocalDate::now);
        var endValue = parseResult.matchedOptionValue("--end", today.toString());
        var endDate = getEndDateFromParamValue(endValue, today);
        final Optional<LocalDate> maybeStart = Optional.ofNullable(parseResult.matchedOptionValue("--start", null));
        final String accountFilter = parseResult.matchedOptionValue("--account", ".*");
        final Optional<String> reportCurrency = Optional.ofNullable(parseResult.matchedOptionValue("--currency", "")).filter(v -> !v.equals(""));
        var selectedColumns = getSelectedColumns(parseResult.matchedOptionValue("--columns", ""));

        var exactValues = parseResult.matchedOptionValue("--exact", false);
        var jsonFormat = parseResult.matchedOptionValue("--json", false);
        final String intervalRaw = parseResult.matchedOptionValue("--interval", null);
        final Optional<PeriodInterval> period = intervalRaw == null
                ? Optional.empty()
                : Optional.of(PeriodInterval.valueOf(intervalRaw.toUpperCase(Locale.ROOT)));

        var group = parseResult.matchedOptionValue("--group", false);
        var onlyDeltas = parseResult.matchedOptionValue("--delta", false);

        return new ReturnsAppParameters(journalsPaths, endDate, maybeStart, accountFilter, reportCurrency, selectedColumns, exactValues, jsonFormat, period, group, onlyDeltas, "TOTAL");
    }

    private List<String> getSelectedColumns(String rawSelectedColumns) {
        return Arrays.stream(rawSelectedColumns.split(","))
                .distinct()
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());
    }

    private static LocalDate getEndDateFromParamValue(String endValue, LocalDate today) {
        return endValue.equals("month")
                ? today.withDayOfMonth(1)
                : LocalDate.parse(endValue);
    }
}