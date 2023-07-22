package beanvest.returns.cli.columns;

import beanvest.tradingjournal.Stat;
import beanvest.tradingjournal.StatsWithDeltas;
import beanvest.tradingjournal.model.UserErrorId;
import beanvest.lib.clitable.Column;
import beanvest.lib.clitable.ColumnPadding;
import beanvest.returns.cli.AccountPeriod;

import java.util.Optional;
import java.util.function.Function;

record PeriodicCashColumnSpec(ColumnId columnId,
                              Function<StatsWithDeltas, Stat> extractor) implements PeriodicColumnSpec {
    @Override
    public Column<AccountPeriod> toColumn(Optional<String> group, String period, boolean exact, boolean delta, String title) {
        return new Column<>(
                group,
                title,
                ColumnPadding.RIGHT,
                accountPeriodStats -> accountPeriodStats.getStats(period)
                        .map(stats -> {
                            if (delta) {
                                return ColumnValueFormatter.formatMoney(exact, extractor.apply(stats).delta());
                            } else {
                                return ColumnValueFormatter.formatMoney(exact, extractor.apply(stats).stat());
                            }
                        })
                        .orElseGet(() -> ColumnValueFormatter.formatError(UserErrorId.ACCOUNT_NOT_OPEN_YET)));
    }

}
