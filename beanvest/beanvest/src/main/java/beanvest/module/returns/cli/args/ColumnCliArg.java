package beanvest.module.returns.cli.args;

import beanvest.module.returns.cli.columns.ColumnId;

import static beanvest.module.returns.cli.columns.ColumnId.*;

public enum ColumnCliArg {
    opened(OPENED, "opening date"),
    closed(CLOSED, "closing date"),
    deps(DEPOSITS, "deposits"),
    wths(WITHDRAWALS, "withdrawals"),
    dw(DEPOSITS_AND_WITHDRAWALS, "deposits plus withdrawals"),
    intr(INTEREST, "interest"),
    fees(FEES, "fees"),
    ifee(INTEREST_FEES, "interest plus fees"),
    hval(HOLDINGS_VALUE, "date value"),
    xirr(XIRR, "xirr"),
    rgain(REALIZED_GAIN, "realized gain"),
    ugain(UNREALIZED_GAIN, "unrealized gain"),
    div(DIVIDENDS, "dividends"),
    again(ACCOUNT_GAIN, "holdings value + cash + withdrawals - deposits"),
    cash(CASH, "cash"),
    value(VALUE, "cash + market value of the holdings");

    public final ColumnId column;
    public final String name;

    ColumnCliArg(ColumnId column, String name) {
        this.column = column;
        this.name = name;
    }
}
