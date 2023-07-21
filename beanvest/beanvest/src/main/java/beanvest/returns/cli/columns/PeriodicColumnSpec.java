package beanvest.returns.cli.columns;

import beanvest.lib.clitable.Column;
import beanvest.returns.cli.AccountPeriod;

import java.util.Optional;

public sealed interface PeriodicColumnSpec permits PeriodicCashColumnSpec, PeriodicValueColumnSpec, PeriodicXirrColumnSpec {
    ColumnId columnId();

    Column<AccountPeriod> toColumn(Optional<String> group, String period, boolean exact, boolean delta, String title);
}
