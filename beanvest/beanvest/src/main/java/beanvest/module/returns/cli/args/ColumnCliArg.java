package beanvest.module.returns.cli.args;

import beanvest.module.returns.StatDefinition;

import static beanvest.module.returns.StatDefinition.*;

public enum ColumnCliArg {
    opened(OPENED, OPENED, "opening date"),
    closed(CLOSED, CLOSED, "closing date"),

    deps(DEPOSITS, DEPOSITS_PERIOD, "deposits"),
    wths(WITHDRAWALS, WITHDRAWALS_PERIOD, "withdrawals"),

    intr(INTEREST, INTEREST_PERIOD, "interest"),
    fees(FEES, FEES_PERIOD, "fees"),

    div(DIVIDENDS, DIVIDENDS_PERIOD, "dividends"),

    rgain(REALIZED_GAIN, REALIZED_GAIN_PERIOD, "realized gain"),
    ugain(UNREALIZED_GAIN, UNREALIZED_GAIN_PERIOD, "unrealized gain"),

    xirr(XIRR, XIRR_PERIOD, "xirr"),

    val(VALUE, VALUE_PERIOD, "date value"),
    cost(NET_COST, NET_COST, "Net cost - gross cost minus benefits (withdrawals, dividends, sale revenue etc)"),
    profit(PROFIT, PROFIT_PERIOD, "value - cost");


    public final StatDefinition column;
    public final StatDefinition periodicColumn;
    public final String name;

    ColumnCliArg(StatDefinition column, StatDefinition periodicColumn, String name) {
        this.column = column;
        this.periodicColumn = periodicColumn;
        this.name = name;
    }
}
