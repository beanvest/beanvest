package beanvest.module.returns.cli.args;

import beanvest.processor.StatDefinition;
import beanvest.processor.CollectionMode;
import beanvest.processor.processingv2.EntitiesToInclude;
import beanvest.processor.time.PeriodInterval;
import picocli.CommandLine;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

public class ReturnsCliParametersRetriever {

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
        var selectedColumns = getCollect(parseResult, onlyDeltas);

        var entitiesToInclude = new EntitiesToInclude(grouping.includesGroups(), grouping.includesAccounts(), reportInvestments);

        var collectionMode = onlyDeltas ? CollectionMode.DELTA : CollectionMode.CUMULATIVE;
        return new ReturnsAppParameters(journalsPaths, endDate, startDate, accountFilter, reportCurrency, selectedColumns.statColumns, selectedColumns.accountMetadataColumns,
                jsonFormat, period,  collectionMode, "TOTAL", entitiesToInclude);
    }

    private SelectedColumns getCollect(CommandLine.ParseResult parseResult, Boolean onlyDeltas) {
        var statColumns = new ArrayList<StatDefinition>();
        var metaColumns = new ArrayList<AccountMetaColumn>();
        for (CliColumnValue cliColumnValue : parseResult.matchedOptionValue("--columns", new CliColumnValue[0])) {
            if (cliColumnValue.cliColumn instanceof StatColumn c) {
                statColumns.add(onlyDeltas ? c.periodicStat() : c.cumulativeStat());
            } else if (cliColumnValue.cliColumn instanceof AccountMetaColumn c) {
                metaColumns.add(c);
            } else {
                throw new IllegalArgumentException("Unsupported column type: " + cliColumnValue.cliColumn.getClass().getName());
            }
        }
        return new SelectedColumns(statColumns, metaColumns);
    }

    private static LocalDate getEndDate(CommandLine.ParseResult parseResult, LocalDate today) {
        var text = parseResult.matchedOptionValue("--end", today.toString());
        if (text.toLowerCase(Locale.ROOT).equals("month")) {
            return today.withDayOfMonth(1);
        }
        return LocalDate.parse(text);
    }

    record SelectedColumns(List<StatDefinition> statColumns, List<AccountMetaColumn> accountMetadataColumns)
    {}
}