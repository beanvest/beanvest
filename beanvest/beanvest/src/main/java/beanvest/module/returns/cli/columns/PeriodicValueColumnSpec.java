package beanvest.module.returns.cli.columns;

import beanvest.processor.StatsWithDeltasDto;
import beanvest.processor.ValueStatDto;
import beanvest.result.UserErrorId;
import beanvest.lib.clitable.Column;
import beanvest.lib.clitable.ColumnPadding;
import beanvest.module.returns.cli.AccountPeriod;

import java.util.Optional;
import java.util.function.Function;

record PeriodicValueColumnSpec(ColumnId columnId,
                               Function<StatsWithDeltasDto, ValueStatDto> extractor) implements PeriodicColumnSpec {
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
