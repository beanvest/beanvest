package beanvest.module.returns.cli.columns;

import beanvest.processor.processingv2.processor.DividendCollector;

public enum ColumnId {
    ACCOUNT("account", "account or group", DividendCollector.class),
    OPENED("opened", "opening date", DividendCollector.class),
    CLOSED("closed", "closing date", DividendCollector.class),
    DEPOSITS("deps", "deposits", DividendCollector.class),
    WITHDRAWALS("wths", "withdrawals", DividendCollector.class),
    DEPOSITS_AND_WITHDRAWALS("dw", "deposits plus withdrawals", DividendCollector.class),
    INTEREST("intr", "interest", DividendCollector.class),
    FEES("fees", "fees", DividendCollector.class),
    INTEREST_FEES("if", "interest plus fees", DividendCollector.class),
    HOLDINGS_VALUE("hVal", "holdings value", DividendCollector.class),
    XIRR("xirr", "internal rate of return (cumulative)", DividendCollector.class),
    XIRRP("xirrp", "periodic (periodic)", DividendCollector.class),
    REALIZED_GAIN("rGain", "realized gain", DividendCollector.class),
    UNREALIZED_GAIN("uGain", "unrealized gain", DividendCollector.class),
    DIVIDENDS("div", "dividends", DividendCollector.class),
    ACCOUNT_GAIN("aGain", "holdings value + cash + withdrawals - deposits", DividendCollector.class),
    CASH("cash", "cash", DividendCollector.class),
    VALUE("value", "cash + market value of the holdings", DividendCollector.class);

    public final String header;
    public final String name;
    public final Class<?> collector;

    ColumnId(String header, String name, Class<?> collector) {
        this.header = header;
        this.name = name;
        this.collector = collector;
    }
}
