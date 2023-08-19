package beanvest.module.returns.cli.args;

import beanvest.module.returns.ReturnsAppParameters;
import beanvest.processor.CollectionMode;
import beanvest.processor.time.PeriodInterval;
import picocli.CommandLine;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReturnsCliParametersParser {

    public ReturnsAppParameters retrieveCliParameters(CommandLine.ParseResult parseResult) {
        var journalsPaths = parseResult.matchedPositionalValue(0, new ArrayList<Path>());
        final Optional<LocalDate> overrideToday = Optional.ofNullable(parseResult.matchedOptionValue("--override-today", null));
        var today = overrideToday.orElseGet(LocalDate::now);
        var endDate = getEndDate(parseResult, today);
        var startDate = parseResult.matchedOptionValue("--startDate", LocalDate.MIN);
        var accountFilter = parseResult.matchedOptionValue("--account", ".*");
        var reportCurrency = Optional.ofNullable(parseResult.matchedOptionValue("--currency", ""));

        var reportInvestments = parseResult.matchedOptionValue("--report-holdings", false);
        var jsonFormat = parseResult.matchedOptionValue("--json", false);
        var period = parseResult.matchedOptionValue("--interval", PeriodInterval.NONE);
        var grouping = parseResult.matchedOptionValue("--groups", AccountGroupingCliArg.DEFAULT).mappedValue;
        var onlyDeltas = parseResult.matchedOptionValue("--delta", false);
        var selectedColumns = Arrays.stream(parseResult.matchedOptionValue("--columns", new ColumnCliArg[0])).map(c -> onlyDeltas ? c.periodicColumn : c.column).collect(Collectors.toList());

        var collectionMode = onlyDeltas ? CollectionMode.DELTA : CollectionMode.CUMULATIVE;
        return new ReturnsAppParameters(journalsPaths, endDate, startDate, accountFilter, reportCurrency, selectedColumns,
                jsonFormat, period, grouping, collectionMode, "TOTAL", reportInvestments);
    }

    private static LocalDate getEndDate(CommandLine.ParseResult parseResult, LocalDate today) {
        var text = parseResult.matchedOptionValue("--end", today.toString());
        if (text.toLowerCase(Locale.ROOT).equals("month")) {
            return today.withDayOfMonth(1);
        }
        return LocalDate.parse(text);
    }
}