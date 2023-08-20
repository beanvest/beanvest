package beanvest.module.returns.cli;

import beanvest.lib.clitable.Column;
import beanvest.lib.clitable.ColumnPadding;
import beanvest.lib.clitable.TableWriter;
import beanvest.module.returns.StatDefinition;
import beanvest.module.returns.cli.args.AccountMetaColumn;
import beanvest.module.returns.cli.columns.CliColumnValueFormatter;
import beanvest.module.returns.cli.columns.ColumnSpec;
import beanvest.module.returns.cli.columns.ValueFormatter;
import beanvest.processor.CollectionMode;
import beanvest.processor.processingv2.dto.AccountDto2;
import beanvest.processor.processingv2.dto.PortfolioStatsDto2;

import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;

public class CliTablePrinter {
    public static final Map<String, CliColumnValueFormatter> CUSTOM_FORMATTERS = Map.of(
            StatDefinition.XIRR.header, ValueFormatter::xirr,
            StatDefinition.XIRR_PERIOD.header, ValueFormatter::xirr
    );
    private final TableWriter tableWriter = new TableWriter().setMinColumnWidth(5);

    public void printCliOutput(
            List<AccountMetaColumn> accountMetadataColumns,
            PortfolioStatsDto2 stats,
            PrintStream output,
            List<String> selectedColumns,
            CollectionMode collectionMode) {
        var periods = new ArrayList<>(stats.periods());
        Collections.reverse(periods);


        List<Column<AccountDto2>> columns = new ArrayList<>();
        columns.add(new Column<>("Account", ColumnPadding.LEFT, AccountDto2::account));
        columns.addAll(createAccountMetaColumns(accountMetadataColumns));
        columns.addAll(createStatsColumns(selectedColumns, collectionMode, periods));

        var writer = new StringWriter();
        try {
            tableWriter.writeTable(writer, stats.accountDtos(), columns);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        output.print(writer);
    }

    private List<Column<AccountDto2>> createAccountMetaColumns(List<AccountMetaColumn> accountMetadataColumns) {
        return accountMetadataColumns.stream()
                .map(col -> new Column<>(col.shortName(), ColumnPadding.RIGHT, col.extractor()))
                .toList();
    }

    private List<Column<AccountDto2>> createStatsColumns(List<String> selectedColumns, CollectionMode collectionMode, List<String> periods) {
        var statsByName = Arrays.stream(StatDefinition.values())
                .collect(Collectors.toMap(s -> s.header, s -> s));

        var mapByIsAccountColumn = selectedColumns.stream()
                .collect(Collectors.groupingBy(s -> statsByName.get(s).type == StatDefinition.StatType.ACCOUNT));

        var columns = new ArrayList<Column<AccountDto2>>();
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

    private CliColumnValueFormatter getFormatter(ColumnSpec spec) {
        return CUSTOM_FORMATTERS.getOrDefault(spec.statsDefinition().header, ValueFormatter::money);
    }
}
