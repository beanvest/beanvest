package beanvest.returns.cli.columns;

import beanvest.tradingjournal.Stats;
import beanvest.tradingjournal.StatsWithDeltas;
import beanvest.lib.clitable.Column;
import beanvest.lib.clitable.ColumnPadding;
import beanvest.returns.cli.AccountPeriod;

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
            new PeriodicCashColumnSpec(ColumnId.DEPOSITS, StatsWithDeltas::deposits),
            new PeriodicCashColumnSpec(ColumnId.WITHDRAWALS, StatsWithDeltas::withdrawals),
            new PeriodicCashColumnSpec(ColumnId.DIVIDENDS, StatsWithDeltas::dividends),
            new PeriodicCashColumnSpec(ColumnId.INTEREST, StatsWithDeltas::interest),
            new PeriodicCashColumnSpec(ColumnId.FEES, StatsWithDeltas::fees),
            new PeriodicCashColumnSpec(ColumnId.REALIZED_GAIN, StatsWithDeltas::realizedGain),
            new PeriodicCashColumnSpec(ColumnId.CASH, StatsWithDeltas::cash),
            new PeriodicValueColumnSpec(ColumnId.UNREALIZED_GAIN, StatsWithDeltas::unrealizedGains),
            new PeriodicValueColumnSpec(ColumnId.HOLDINGS_VALUE, StatsWithDeltas::holdingsValue),
            new PeriodicValueColumnSpec(ColumnId.ACCOUNT_GAIN, StatsWithDeltas::accountGain),
            new PeriodicXirrColumnSpec(ColumnId.XIRR, Stats::xirr)
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
