package beanvest.module.returns.cli.columns;

import beanvest.lib.clitable.Column;
import beanvest.lib.clitable.ColumnPadding;
import beanvest.module.returns.StatDefinition;
import beanvest.processor.dto.AccountPeriodDto;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class ReportColumnsDefinition {
    public static final String ALWAYS_VISIBLE = "account";
    public static final List<Column<AccountPeriodDto>> COLUMNS_BASIC = List.of(
            new Column<>(ALWAYS_VISIBLE, ColumnPadding.LEFT, AccountPeriodDto::account),
            new Column<>(StatDefinition.OPENED.header, ColumnPadding.LEFT, r -> r.openingDate().toString()),
            new Column<>(StatDefinition.CLOSED.header, ColumnPadding.LEFT, r -> r.closingDate().map(LocalDate::toString).orElse("-")));

    public static final List<ColumnSpec> COLUMNS = Arrays.stream(StatDefinition.values()).map(ColumnSpec::new).toList();
    public static final List<StatDefinition> DEFAULT_COLUMNS_SINGLE_PERIOD = List.of(StatDefinition.XIRR, StatDefinition.PROFIT);
    public static final List<StatDefinition> DEFAULT_COLUMNS_MULTIPLE_PERIODS = Arrays.stream(StatDefinition.values()).toList();
}
