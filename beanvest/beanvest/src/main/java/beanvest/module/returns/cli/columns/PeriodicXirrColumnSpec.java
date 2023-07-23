package beanvest.module.returns.cli.columns;

import beanvest.lib.clitable.Column;
import beanvest.lib.clitable.ColumnPadding;
import beanvest.module.returns.cli.AccountPeriod;
import beanvest.processor.StatsWithDeltasDto;
import beanvest.processor.ValueStatDto;
import beanvest.result.UserErrorId;

import java.util.Optional;
import java.util.function.Function;

record PeriodicXirrColumnSpec(ColumnId columnId, XirrResultExtractor extractor) implements PeriodicColumnSpec {
    public Column<AccountPeriod> toColumn(Optional<String> group, String period, boolean exact, boolean delta, String title) {
        return new Column<>(
                group,
                title,
                ColumnPadding.RIGHT,
                accountPeriod -> convertResultToString(accountPeriod.getStats(period)));
    }

    private String convertResultToString(Optional<StatsWithDeltasDto> maybeStats) {
        return maybeStats.map(stats -> extractor.apply(stats).stat().fold(
                        gainValue -> ColumnValueFormatter.formatXirr(gainValue.doubleValue()),
                        ColumnValueFormatter::formatError))
                .orElse(ColumnValueFormatter.formatError(UserErrorId.ACCOUNT_NOT_OPEN_YET));
    }


    interface XirrResultExtractor extends Function<StatsWithDeltasDto, ValueStatDto> {
    }
}
