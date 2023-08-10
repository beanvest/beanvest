package beanvest.module.returns.cli.columns;

import beanvest.result.ErrorEnum;
import beanvest.lib.clitable.Column;
import beanvest.lib.clitable.ColumnPadding;
import beanvest.processor.dto.AccountPeriodDto;

import java.util.Optional;

public record ColumnSpec(ColumnId columnId) {
    public Column<AccountPeriodDto> toColumn(Optional<String> group, String period, boolean exact, String title) {
        return new Column<>(
                group,
                title,
                ColumnPadding.RIGHT,
                accountPeriodStats -> accountPeriodStats.getStats(period)
                        .map(stats -> ColumnValueFormatter.formatMoney(exact, stats.stats().get(columnId.header)))
                        .orElseGet(() -> ColumnValueFormatter.formatError(ErrorEnum.ACCOUNT_NOT_OPEN_YET)));
    }

}
