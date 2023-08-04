package beanvest.module.returns.cli.columns;

import beanvest.processor.processingv2.processor.DividendCalculator;
import beanvest.processor.processingv2.processor.PeriodDividendCalculator;

public enum ColumnId {
    ACCOUNT("account", "account or group", null),
    OPENED("opened", "opening date", null),
    CLOSED("closed", "closing date", null),
    DEPOSITS("deps", "deposits", null),
    WITHDRAWALS("wths", "withdrawals", null),
    DEPOSITS_AND_WITHDRAWALS("dw", "deposits plus withdrawals", null),
    INTEREST("intr", "interest", null),
    FEES("fees", "fees", null),
    INTEREST_FEES("if", "interest plus fees", null),
    HOLDINGS_VALUE("hVal", "holdings value", null),
    XIRR("xirr", "internal rate of return (cumulative)", null),
    XIRR_PERIOD("xirrp", "periodic (periodic)", null),
    REALIZED_GAIN("rGain", "realized gain", null),
    UNREALIZED_GAIN("uGain", "unrealized gain", null),
    DIVIDENDS("cDiv", "dividends cumulative", DividendCalculator.class),
    DIVIDENDS_PERIOD("pDiv", "dividends per period", PeriodDividendCalculator.class),
    ACCOUNT_GAIN("aGain", "holdings value + cash + withdrawals - deposits", null),
    CASH("cash", "cash", null),
    VALUE("value", "cash + market value of the holdings", null);

    public final String header;
    public final String name;
    public final Class<?> calculator;

    ColumnId(String header, String name, Class<?> calculator) {
        this.header = header;
        this.name = name;
        this.calculator = calculator;
    }
}
