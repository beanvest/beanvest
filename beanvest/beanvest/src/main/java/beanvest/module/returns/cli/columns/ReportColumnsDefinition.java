package beanvest.module.returns.cli.columns;

import beanvest.lib.clitable.Column;
import beanvest.lib.clitable.ColumnPadding;
import beanvest.module.returns.StatDefinition;
import beanvest.processor.processingv2.dto.AccountDto2;

import java.time.LocalDate;
import java.util.Map;

public class ReportColumnsDefinition {
    public static final String ACCOUNT_COLUMN = "Account";
    public static final Map<String, Column<AccountDto2>> COLUMNS_ACCOUNT = Map.of(
            "Account", new Column<>(ACCOUNT_COLUMN, ColumnPadding.LEFT, AccountDto2::account),
            "Opened", new Column<>(StatDefinition.OPENED.header, ColumnPadding.LEFT, r -> r.openingDate().toString()),
            "Closed", new Column<>(StatDefinition.CLOSED.header, ColumnPadding.LEFT, r -> r.closingDate().map(LocalDate::toString).orElse("-")));
}
