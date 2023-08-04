package beanvest.module.returns.cli.columns;

import beanvest.lib.clitable.Column;
import beanvest.lib.clitable.ColumnPadding;
import beanvest.processor.dto.AccountPeriodDto;
import beanvest.processor.dto.StatsWithDeltasDto;
import beanvest.processor.dto.ValueStatDto;
import beanvest.processor.processingv2.StatsV2;
import beanvest.result.ErrorEnum;

import java.util.Optional;
import java.util.function.Function;

record PeriodicXirrColumnSpec(ColumnId columnId, XirrResultExtractor extractor) implements PeriodicColumnSpec {
    public Column<AccountPeriodDto> toColumn(Optional<String> group, String period, boolean exact, boolean delta, String title) {
        return new Column<>(
                group,
                title,
                ColumnPadding.RIGHT,
                accountPeriod -> convertResultToString(accountPeriod.getStats(period)));
    }

    private String convertResultToString(Optional<StatsV2> maybeStats) {
        return maybeStats.map(stats -> extractor.apply(stats).stat().fold(
                        gainValue -> ColumnValueFormatter.formatXirr(gainValue.doubleValue()),
                        ColumnValueFormatter::formatError))
                .orElse(ColumnValueFormatter.formatError(ErrorEnum.ACCOUNT_NOT_OPEN_YET));
    }


    interface XirrResultExtractor extends Function<StatsV2, ValueStatDto> {
    }
}
