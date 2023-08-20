package beanvest.module.returns.cli.args;

import beanvest.module.returns.cli.columns.ValueFormatter;

import static beanvest.module.returns.StatDefinition.*;

public enum CliColumnValue {
    opened(new AccountMetaColumn("Opened", "Opening date", ValueFormatter::openedDate)),
    closed(new AccountMetaColumn("Closed", "Closing date", ValueFormatter::closedDate)),

    deps(new StatColumn(DEPOSITS, DEPOSITS_PERIOD, "Deposits", ValueFormatter::money)),
    wths(new StatColumn(WITHDRAWALS, WITHDRAWALS_PERIOD, "Withdrawals", ValueFormatter::money)),
    dw(new StatColumn(DEPOSITS_AND_WITHDRAWALS, DEPOSITS_AND_WITHDRAWALS_PERIOD, "Withdrawals", ValueFormatter::money)),

    intr(new StatColumn(INTEREST, INTEREST_PERIOD, "Interest", ValueFormatter::money)),
    fees(new StatColumn(FEES, FEES_PERIOD, "Fees", ValueFormatter::money)),

    div(new StatColumn(DIVIDENDS, DIVIDENDS_PERIOD, "Dividends", ValueFormatter::money)),

    rgain(new StatColumn(REALIZED_GAIN, REALIZED_GAIN_PERIOD, "Realized gain", ValueFormatter::money)),
    ugain(new StatColumn(UNREALIZED_GAIN, UNREALIZED_GAIN_PERIOD, "Unrealized gain", ValueFormatter::money)),
    again(new StatColumn(ACCOUNT_GAIN, ACCOUNT_GAIN_PERIOD, "Account gain", ValueFormatter::money)),

    xirr(new StatColumn(XIRR, XIRR_PERIOD, "Annualized return", ValueFormatter::xirr)),

    value(new StatColumn(VALUE, VALUE_PERIOD, "Value", ValueFormatter::money)),
    cost(new StatColumn(NET_COST, NET_COST, "Cost", ValueFormatter::money)),
    profit(new StatColumn(PROFIT, PROFIT_PERIOD, "Profit", ValueFormatter::money));

    public final CliColumn cliColumn;

    CliColumnValue(CliColumn cliColumn) {
        this.cliColumn = cliColumn;
    }
}
