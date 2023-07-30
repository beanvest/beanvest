package beanvest.journal.entry;

public sealed interface HoldingOperation extends AccountOperation permits Buy, Sell, Dividend {
    String commodity();
}
