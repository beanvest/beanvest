package beanvest.module.returns.cli.columns;

import beanvest.processor.StatsWithDeltasDto;
import beanvest.lib.clitable.Column;
import beanvest.lib.clitable.ColumnPadding;
import beanvest.module.returns.cli.AccountPeriod;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class ReportColumnsDefinition {
    public static final String ALWAYS_VISIBLE = "account";
    public static final List<Column<AccountPeriod>> COLUMNS_BASIC = List.of(
            new Column<>(ALWAYS_VISIBLE, ColumnPadding.LEFT, AccountPeriod::account),
            new Column<>(ColumnId.OPENED.header, ColumnPadding.LEFT, r -> r.openingDate().toString()),
            new Column<>(ColumnId.CLOSED.header, ColumnPadding.LEFT, r -> r.closingDate().map(LocalDate::toString).orElse("-")));

    public static final List<PeriodicColumnSpec> COLUMNS = List.of(
            new PeriodicCashColumnSpec(ColumnId.DEPOSITS, StatsWithDeltasDto::deposits),
            new PeriodicCashColumnSpec(ColumnId.WITHDRAWALS, StatsWithDeltasDto::withdrawals),
            new PeriodicCashColumnSpec(ColumnId.DIVIDENDS, StatsWithDeltasDto::dividends),
            new PeriodicCashColumnSpec(ColumnId.INTEREST, StatsWithDeltasDto::interest),
            new PeriodicCashColumnSpec(ColumnId.FEES, StatsWithDeltasDto::fees),
            new PeriodicCashColumnSpec(ColumnId.REALIZED_GAIN, StatsWithDeltasDto::realizedGain),
            new PeriodicCashColumnSpec(ColumnId.CASH, StatsWithDeltasDto::cash),
            new PeriodicValueColumnSpec(ColumnId.UNREALIZED_GAIN, StatsWithDeltasDto::unrealizedGains),
            new PeriodicValueColumnSpec(ColumnId.HOLDINGS_VALUE, StatsWithDeltasDto::holdingsValue),
            new PeriodicValueColumnSpec(ColumnId.ACCOUNT_GAIN, StatsWithDeltasDto::accountGain),
            new PeriodicXirrColumnSpec(ColumnId.XIRR, StatsWithDeltasDto::xirr)
    );
    public static final List<ColumnId> COLUMNS_NEEDING_VALUATION = List.of(
            ColumnId.UNREALIZED_GAIN,
            ColumnId.VALUE,
            ColumnId.ACCOUNT_GAIN,
            ColumnId.XIRR
    );
    public static final List<ColumnId> DEFAULT_COLUMNS_SINGLE_PERIOD = List.of(ColumnId.XIRR, ColumnId.ACCOUNT_GAIN);
    public static final List<ColumnId> DEFAULT_COLUMNS_MULTIPLE_PERIODS = Arrays.stream(ColumnId.values()).toList();
}
