package beanvest.module.returns.cli.args;

import beanvest.module.returns.StatDefinition;

import static beanvest.module.returns.StatDefinition.*;

public enum ColumnCliArg {
    opened(OPENED, OPENED, "Opening date"),
    closed(CLOSED, CLOSED, "Closing date"),

    deps(DEPOSITS, DEPOSITS_PERIOD, "Deposits"),
    wths(WITHDRAWALS, WITHDRAWALS_PERIOD, "Withdrawals"),
    dw(DEPOSITS_AND_WITHDRAWALS, DEPOSITS_AND_WITHDRAWALS_PERIOD, "Withdrawals"),

    intr(INTEREST, INTEREST_PERIOD, "Interest"),
    fees(FEES, FEES_PERIOD, "Fees"),

    div(DIVIDENDS, DIVIDENDS_PERIOD, "Dividends"),

    rgain(REALIZED_GAIN, REALIZED_GAIN_PERIOD, "Realized gain"),
    ugain(UNREALIZED_GAIN, UNREALIZED_GAIN_PERIOD, "Unrealized gain"),

    xirr(XIRR, XIRR_PERIOD, "Annualized return"),

    value(VALUE, VALUE_PERIOD, "Value"),
    cost(NET_COST, NET_COST, "Cost"),
    profit(PROFIT, PROFIT_PERIOD, "Profit");


    public final StatDefinition column;
    public final StatDefinition periodicColumn;
    public final String fullName;

    ColumnCliArg(StatDefinition column, StatDefinition periodicColumn, String fullName) {
        this.column = column;
        this.periodicColumn = periodicColumn;
        this.fullName = fullName;
    }
}
