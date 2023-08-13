package beanvest.module.returns.cli;

import beanvest.lib.clitable.Column;
import beanvest.lib.clitable.TableWriter;
import beanvest.module.returns.cli.columns.ColumnSpec;
import beanvest.module.returns.cli.columns.ReportColumnsDefinition;
import beanvest.processor.CollectionMode;
import beanvest.processor.dto.AccountPeriodDto;
import beanvest.processor.processingv2.dto.AccountDto2;
import beanvest.processor.processingv2.dto.PortfolioStatsDto2;
import beanvest.processor.time.Period;

import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Stream;

public class CliTablePrinter {
    private final Boolean exact;
    private final TableWriter tableWriter = new TableWriter().setMinColumnWidth(5);

    public CliTablePrinter(Boolean exact) {
        this.exact = exact;
    }

    public void printCliOutput(PortfolioStatsDto2 stats, PrintStream output, List<String> selectedColumns, CollectionMode collectionMode) {
        List<Column<AccountPeriodDto>> columns = createColumns(selectedColumns, collectionMode, stats.periods());

        var rows = stats.accountDtos().stream()
                .sorted(Comparator.comparing(AccountDto2::account))
                .map(accStats -> new AccountPeriodDto(accStats.account(), accStats.openingDate(), accStats.closingDate(), accStats.periodStats()))
                .toList();

        var writer = new StringWriter();
        try {
            tableWriter.writeTable(writer, rows, columns);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        output.print(writer);
    }

    private List<Column<AccountPeriodDto>> createColumns(List<String> selectedColumns, CollectionMode collectionMode, List<String> periods) {
        var lowercaseSelectedColumns = prepareSelectedColumnsOrGetDefault(selectedColumns, periods.size());

        var columns = new ArrayList<>(getMainColumns(lowercaseSelectedColumns));
        if (collectionMode == CollectionMode.CUMULATIVE && periods.size() == 1) {
            columns.addAll(createPeriodicColumns(lowercaseSelectedColumns, "TOTAL", Optional.empty(), collectionMode));
        } else {
            periods.stream().sorted(Comparator.reverseOrder())
                    .filter(Objects::nonNull)
                    .forEach(period -> columns.addAll(
                            createPeriodicColumns(lowercaseSelectedColumns, period, Optional.of(period), collectionMode))
                    );
        }
        return columns;
    }

    private List<Column<AccountPeriodDto>> createPeriodicColumns(List<String> selectedColumns, String period, Optional<String> group, CollectionMode collectionMode) {
        var deltas = collectionMode == CollectionMode.DELTA;
        return streamSelectedColumns(selectedColumns)
                .map(spec -> spec.toColumn(group, period, exact, spec.statsDefinition().header))
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
                .map(column -> column.header)
                .toList();
    }

    private static Stream<ColumnSpec> streamSelectedColumns(List<String> selectedColumnsOrDefaultSet) {
        return ReportColumnsDefinition.COLUMNS.stream()
                .filter(col -> selectedColumnsOrDefaultSet.contains(col.statsDefinition().header.toLowerCase(Locale.ROOT)));
    }

    private static List<Column<AccountPeriodDto>> getMainColumns(List<String> selectedColumns) {
        var columnsToKeep = new ArrayList<>(selectedColumns);
        columnsToKeep.add(ReportColumnsDefinition.ALWAYS_VISIBLE);
        return ReportColumnsDefinition.COLUMNS_BASIC.stream()
                .filter(c -> columnsToKeep.contains(c.name()))
                .toList();
    }

}
