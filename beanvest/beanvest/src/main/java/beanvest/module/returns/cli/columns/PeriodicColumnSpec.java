package beanvest.module.returns.cli.columns;

import beanvest.lib.clitable.Column;
import beanvest.processor.dto.AccountPeriodDto;

import java.util.Optional;

public sealed interface PeriodicColumnSpec permits ColumnSpec, PeriodicValueColumnSpec, PeriodicXirrColumnSpec {
    ColumnId columnId();

    Column<AccountPeriodDto> toColumn(Optional<String> group, String period, boolean exact, boolean delta, String title);
}
