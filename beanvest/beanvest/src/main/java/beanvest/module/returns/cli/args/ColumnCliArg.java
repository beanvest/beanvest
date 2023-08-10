package beanvest.module.returns.cli.args;

import beanvest.module.returns.cli.columns.ColumnId;

import static beanvest.module.returns.cli.columns.ColumnId.*;

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


    public final ColumnId column;
    public final ColumnId periodicColumn;
    public final String name;

    ColumnCliArg(ColumnId column, ColumnId periodicColumn, String name) {
        this.column = column;
        this.periodicColumn = periodicColumn;
        this.name = name;
    }
}
