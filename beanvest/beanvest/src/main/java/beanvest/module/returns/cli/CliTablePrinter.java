package beanvest.module.returns.cli;

import beanvest.lib.clitable.Column;
import beanvest.lib.clitable.TableWriter;
import beanvest.module.returns.StatDefinition;
import beanvest.module.returns.cli.columns.ColumnSpec;
import beanvest.module.returns.cli.columns.ColumnValueFormatter;
import beanvest.module.returns.cli.columns.ReportColumnsDefinition;
import beanvest.processor.CollectionMode;
import beanvest.processor.processingv2.dto.AccountDto2;
import beanvest.processor.processingv2.dto.PortfolioStatsDto2;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CliTablePrinter {
    public static final Map<String, Function<Result<BigDecimal, UserErrors>, String>> CUSTOM_FORMATTERS = Map.of(
            StatDefinition.XIRR.header, ColumnValueFormatter::formatXirr
    );
    private final TableWriter tableWriter = new TableWriter().setMinColumnWidth(5);

    public CliTablePrinter(Boolean exact) {
    }

    public void printCliOutput(PortfolioStatsDto2 stats, PrintStream output, List<String> selectedColumns, CollectionMode collectionMode) {
        var periods = new ArrayList<>(stats.periods());
        Collections.reverse(periods);

        var columns = createColumns(selectedColumns, collectionMode, periods);

        var writer = new StringWriter();
        try {
            tableWriter.writeTable(writer, stats.accountDtos(), columns);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        output.print(writer);
    }

    private List<Column<AccountDto2>> createColumns(List<String> selectedColumns, CollectionMode collectionMode, List<String> periods) {
        var statsByName = Arrays.stream(StatDefinition.values())
                .collect(Collectors.toMap(s -> s.header, s -> s));

        var mapByIsAccountColumn = selectedColumns.stream()
                .collect(Collectors.groupingBy(s -> statsByName.get(s).type == StatDefinition.StatType.ACCOUNT));

        var columns = new ArrayList<Column<AccountDto2>>();
        columns.add(ReportColumnsDefinition.COLUMNS_ACCOUNT.get(ReportColumnsDefinition.ACCOUNT_COLUMN));
        columns.addAll(createAccountColumns(mapByIsAccountColumn.getOrDefault(true, List.of())));
        columns.addAll(createNonAccountColumns(collectionMode, periods, mapByIsAccountColumn.getOrDefault(false, List.of())));
        return columns;
    }

    private List<Column<AccountDto2>> createNonAccountColumns(CollectionMode collectionMode, List<String> periods, List<String> otherColumns) {
        if (collectionMode == CollectionMode.CUMULATIVE && periods.size() == 1) {
            return createPeriodicColumns(otherColumns, "TOTAL", Optional.empty(), collectionMode);

        } else {
            var result = new ArrayList<Column<AccountDto2>>();
            for (String period : periods) {
                result.addAll(createPeriodicColumns(otherColumns, period, Optional.of(period), collectionMode));
            }
            return result;
        }
    }

    private ArrayList<Column<AccountDto2>> createAccountColumns(List<String> columnNames) {
        var columns2 = new ArrayList<Column<AccountDto2>>();
        for (String stat : columnNames) {
            columns2.add(ReportColumnsDefinition.COLUMNS_ACCOUNT.get(stat));
        }
        return columns2;
    }

    private List<Column<AccountDto2>> createPeriodicColumns(List<String> selectedColumns, String period, Optional<String> group, CollectionMode collectionMode) {
        var statsByName = Arrays.stream(StatDefinition.values())
                .collect(Collectors.toMap(s -> s.header, s -> s));

        return selectedColumns
                .stream()
                .map(statsByName::get)
                .filter(Objects::nonNull)
                .map(ColumnSpec::new)
                .map(spec -> spec.toColumn(group, period, spec.statsDefinition().header, getFormatter(spec)))
                .toList();
    }

    private Function<Result<BigDecimal, UserErrors>, String> getFormatter(ColumnSpec spec) {
        return CUSTOM_FORMATTERS.getOrDefault(spec.statsDefinition().header, (s) -> ColumnValueFormatter.formatMoney(false, s));
    }

}
