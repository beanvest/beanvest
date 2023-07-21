package beanvest.returns.cli;

import beanvest.tradingjournal.Period;
import beanvest.tradingjournal.PortfolioStats;
import beanvest.tradingjournal.CollectionMode;
import beanvest.lib.clitable.Column;
import beanvest.lib.clitable.TableWriter;
import beanvest.returns.cli.columns.ColumnId;
import beanvest.returns.cli.columns.PeriodicColumnSpec;
import beanvest.returns.cli.columns.ReportColumnsDefinition;

import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class CliTablePrinter implements ValuationNeededChecker {

    public static final String SYMBOL_DELTA = "Δ";
    private final Boolean exact;
    private final TableWriter tableWriter = new TableWriter().setMinColumnWidth(5);

    public CliTablePrinter(Boolean exact) {
        this.exact = exact;
    }

    public void printCliOutput(PortfolioStats stats, PrintStream output, List<String> selectedColumns) {
        List<Column<AccountPeriod>> columns = createColumns(selectedColumns, stats.collectionMode, stats.periods);

        var rows = stats.stats.stream()
                .sorted(Comparator.comparing(accStats -> accStats.account))
                .map(accStats -> new AccountPeriod(accStats.account, accStats.openingDate, accStats.closingDate, accStats.periodStats))
                .toList();

        var writer = new StringWriter();
        try {
            tableWriter.writeTable(writer, rows, columns);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        output.print(writer);
    }

    @Override
    public boolean isValuationNeeded(List<String> selectedColumns, int periodsCount) {
        var selectedColumnsIds = prepareSelectedColumnsOrGetDefault(selectedColumns, periodsCount);
        return streamSelectedColumns(selectedColumnsIds)
                .anyMatch(s -> ReportColumnsDefinition.COLUMNS_NEEDING_VALUATION.contains(s.columnId()));
    }

    private List<Column<AccountPeriod>> createColumns(List<String> selectedColumns, CollectionMode collectionMode, List<Period> periods) {
        var periodTitles = periods.stream().map(Period::title).toList();
        var lowercaseSelectedColumns = prepareSelectedColumnsOrGetDefault(selectedColumns, periodTitles.size());

        var columns = new ArrayList<>(getMainColumns(lowercaseSelectedColumns));
        if (collectionMode == CollectionMode.CUMULATIVE && periods.size() == 1) {
            columns.addAll(createPeriodicColumns(lowercaseSelectedColumns, "TOTAL", Optional.empty(), collectionMode));
        } else {
            periods.stream().sorted(Comparator.reverseOrder())
                    .filter(period -> period.title() != null)
                    .forEach(period -> columns.addAll(
                            createPeriodicColumns(lowercaseSelectedColumns, period.title(), Optional.of(period.title()), collectionMode))
                    );
        }
        return columns;
    }

    private List<Column<AccountPeriod>> createPeriodicColumns(List<String> selectedColumns, String period, Optional<String> group, CollectionMode collectionMode) {
        var deltas = collectionMode == CollectionMode.DELTA;
        final Function<ColumnId, String> title = (ColumnId columnId) -> (deltas ? SYMBOL_DELTA : "") + columnId.header;
        return streamSelectedColumns(selectedColumns)
                .map(spec -> spec.toColumn(group, period, exact, deltas, title.apply(spec.columnId())))
                .toList();
    }

    private List<String> prepareSelectedColumnsOrGetDefault(List<String> selectedColumns, int size) {
        var columns = selectedColumns.size() > 0 ? selectedColumns : getDefaultColumns(size);
        return columns.stream()
                .map(s -> s.toLowerCase(Locale.ROOT))
                .toList();
    }

    private List<String> getDefaultColumns(int periodsCount) {
        var defaultColumns = periodsCount > 1
                ? ReportColumnsDefinition.DEFAULT_COLUMNS_SINGLE_PERIOD
                : ReportColumnsDefinition.DEFAULT_COLUMNS_MULTIPLE_PERIODS;
        return defaultColumns.stream()
                .map(column -> column.id)
                .toList();
    }

    private static Stream<PeriodicColumnSpec> streamSelectedColumns(List<String> selectedColumnsOrDefaultSet) {
        return ReportColumnsDefinition.COLUMNS.stream()
                .filter(col -> selectedColumnsOrDefaultSet.contains(col.columnId().header.toLowerCase(Locale.ROOT)));
    }

    private static List<Column<AccountPeriod>> getMainColumns(List<String> selectedColumns) {
        var columnsToKeep = new ArrayList<>(selectedColumns);
        columnsToKeep.add(ReportColumnsDefinition.ALWAYS_VISIBLE);
        return ReportColumnsDefinition.COLUMNS_BASIC.stream()
                .filter(c -> columnsToKeep.contains(c.name()))
                .toList();
    }

}
