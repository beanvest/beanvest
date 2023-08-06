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

    public static final List<PeriodicColumnSpec> COLUMNS = List.of(
            new ColumnSpec(ColumnId.DEPOSITS),
            new ColumnSpec(ColumnId.DEPOSITS_PERIOD),
            new ColumnSpec(ColumnId.WITHDRAWALS),
            new ColumnSpec(ColumnId.WITHDRAWALS_PERIOD),

            new ColumnSpec(ColumnId.INTEREST),
            new ColumnSpec(ColumnId.INTEREST_PERIOD),
            new ColumnSpec(ColumnId.FEES),
            new ColumnSpec(ColumnId.FEES_PERIOD),

            new ColumnSpec(ColumnId.DIVIDENDS),
            new ColumnSpec(ColumnId.DIVIDENDS_PERIOD),

            new ColumnSpec(ColumnId.REALIZED_GAIN),
            new ColumnSpec(ColumnId.REALIZED_GAIN_PERIOD),
            new ColumnSpec(ColumnId.UNREALIZED_GAIN),
            new ColumnSpec(ColumnId.UNREALIZED_GAIN_PERIOD),

            new ColumnSpec(ColumnId.VALUE),
            new ColumnSpec(ColumnId.VALUE_PERIOD),
            new ColumnSpec(ColumnId.PROFIT),
            new ColumnSpec(ColumnId.PROFIT_PERIOD),
            new ColumnSpec(ColumnId.XIRR),
            new ColumnSpec(ColumnId.XIRR_PERIOD)
    );
    public static final List<ColumnId> DEFAULT_COLUMNS_SINGLE_PERIOD = List.of(ColumnId.XIRR, ColumnId.PROFIT);
    public static final List<ColumnId> DEFAULT_COLUMNS_MULTIPLE_PERIODS = Arrays.stream(ColumnId.values()).toList();
}
