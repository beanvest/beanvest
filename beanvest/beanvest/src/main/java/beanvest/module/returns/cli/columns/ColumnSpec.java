package beanvest.module.returns.cli.columns;

import beanvest.module.returns.StatDefinition;
import beanvest.processor.processingv2.dto.AccountDto2;
import beanvest.result.ErrorEnum;
import beanvest.lib.clitable.Column;
import beanvest.lib.clitable.ColumnPadding;

import java.util.Optional;

public record ColumnSpec(StatDefinition statsDefinition) {
    public Column<AccountDto2> toColumn(Optional<String> group, String period, String title, CliColumnValueFormatter formatter) {
        return new Column<>(
                group,
                title,
                ColumnPadding.RIGHT,
                accountPeriodStats -> Optional.ofNullable(accountPeriodStats.periodStats().get(period))
                        .map(stats -> stats.stats()
                                .get(statsDefinition.header)
                                .fold(
                                        formatter::format,
                                        ValueFormatter::formatError
                                ))
                        .orElseGet(() -> ValueFormatter.formatError(ErrorEnum.ACCOUNT_NOT_OPEN_YET)));
    }
}