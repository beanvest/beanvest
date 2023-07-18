package beanvest.returns.cli.columns;

import beanvest.tradingjournal.StatsWithDeltas;
import beanvest.tradingjournal.ValueStat;
import beanvest.tradingjournal.model.UserErrorId;
import bb.lib.clitable.Column;
import bb.lib.clitable.ColumnPadding;
import beanvest.returns.cli.AccountPeriod;

import java.util.Optional;
import java.util.function.Function;

record PeriodicValueColumnSpec(ColumnId columnId,
                               Function<StatsWithDeltas, ValueStat> extractor) implements PeriodicColumnSpec {
    @Override
    public Column<AccountPeriod> toColumn(Optional<String> group, String period, boolean exact, boolean delta, String title) {
        return new Column<>(
                group,
                title,
                ColumnPadding.RIGHT,
                accountPeriodStats -> accountPeriodStats.getStats(period)
                        .map(stats -> ColumnValueFormatter.formatMoney(exact, extractor.apply(stats).stat()))
                        .orElseGet(() -> ColumnValueFormatter.formatError(UserErrorId.ACCOUNT_NOT_OPEN_YET)));
    }
}
