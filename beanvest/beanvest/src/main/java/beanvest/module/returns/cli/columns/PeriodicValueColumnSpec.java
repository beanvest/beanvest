package beanvest.module.returns.cli.columns;

import beanvest.processor.dto.StatsWithDeltasDto;
import beanvest.processor.dto.ValueStatDto;
import beanvest.result.ErrorEnum;
import beanvest.lib.clitable.Column;
import beanvest.lib.clitable.ColumnPadding;
import beanvest.processor.dto.AccountPeriodDto;

import java.util.Optional;
import java.util.function.Function;

record PeriodicValueColumnSpec(ColumnId columnId,
                               Function<StatsWithDeltasDto, ValueStatDto> extractor) implements PeriodicColumnSpec {
    @Override
    public Column<AccountPeriodDto> toColumn(Optional<String> group, String period, boolean exact, boolean delta, String title) {
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
                        .orElseGet(() -> ColumnValueFormatter.formatError(ErrorEnum.ACCOUNT_NOT_OPEN_YET)));
    }
}
