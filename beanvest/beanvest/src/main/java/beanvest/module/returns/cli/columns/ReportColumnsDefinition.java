package beanvest.module.returns.cli.columns;

import beanvest.lib.clitable.Column;
import beanvest.lib.clitable.ColumnPadding;
import beanvest.processor.dto.AccountPeriodDto;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class ReportColumnsDefinition {
    public static final String ALWAYS_VISIBLE = "account";
    public static final List<Column<AccountPeriodDto>> COLUMNS_BASIC = List.of(
            new Column<>(ALWAYS_VISIBLE, ColumnPadding.LEFT, AccountPeriodDto::account),
            new Column<>(ColumnId.OPENED.header, ColumnPadding.LEFT, r -> r.openingDate().toString()),
            new Column<>(ColumnId.CLOSED.header, ColumnPadding.LEFT, r -> r.closingDate().map(LocalDate::toString).orElse("-")));

    public static final List<ColumnSpec> COLUMNS = Arrays.stream(ColumnId.values()).map(ColumnSpec::new).toList();
    public static final List<ColumnId> DEFAULT_COLUMNS_SINGLE_PERIOD = List.of(ColumnId.XIRR, ColumnId.PROFIT);
    public static final List<ColumnId> DEFAULT_COLUMNS_MULTIPLE_PERIODS = Arrays.stream(ColumnId.values()).toList();
}
