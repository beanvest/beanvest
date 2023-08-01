package beanvest.module.returns.cli.columns;

public enum ColumnId {
    ACCOUNT("account", "account or group"),
    OPENED("opened", "opening date"),
    CLOSED("closed", "closing date"),
    DEPOSITS("deps", "deposits"),
    WITHDRAWALS("wths", "withdrawals"),
    DEPOSITS_AND_WITHDRAWALS("dw", "deposits plus withdrawals"),
    INTEREST("intr", "interest"),
    FEES("fees", "fees"),
    INTEREST_FEES("if", "interest plus fees"),
    HOLDINGS_VALUE("hVal", "holdings value"),
    XIRR("xirr", "internal rate of return (cumulative)"),
    XIRRP("xirrp", "periodic (periodic)"),
    REALIZED_GAIN("rGain", "realized gain"),
    UNREALIZED_GAIN("uGain", "unrealized gain"),
    DIVIDENDS("div", "dividends"),
    ACCOUNT_GAIN("aGain", "holdings value + cash + withdrawals - deposits"),
    CASH("cash", "cash"),
    VALUE("value", "cash + market value of the holdings");

    public final String header;
    public final String name;

    ColumnId(String header, String name) {
        this.header = header;
        this.name = name;
    }
}
