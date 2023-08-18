package beanvest.module.returns.cli.columns;

import beanvest.module.returns.StatDefinition;
import beanvest.processor.processingv2.dto.AccountDto2;
import beanvest.processor.processingv2.dto.StatsV2;
import beanvest.result.ErrorEnum;
import beanvest.lib.clitable.Column;
import beanvest.lib.clitable.ColumnPadding;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Function;

public record ColumnSpec(StatDefinition statsDefinition) {
    public Column<AccountDto2> toColumn(Optional<String> group, String period, String title, Function<Result<BigDecimal, UserErrors>, String> formatter) {
        return new Column<>(
                group,
                title,
                ColumnPadding.RIGHT,
                accountPeriodStats -> Optional.ofNullable(accountPeriodStats.periodStats().get(period))
                        .map(stats -> formatter.apply(getStat(stats)))
                        .orElseGet(() -> ColumnValueFormatter.formatError(ErrorEnum.ACCOUNT_NOT_OPEN_YET)));
    }

    private Result<BigDecimal, UserErrors> getStat(StatsV2 stats) {
        return stats.stats().get(statsDefinition.header);
    }
}