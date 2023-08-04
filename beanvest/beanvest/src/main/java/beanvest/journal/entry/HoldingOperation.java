package beanvest.journal.entry;

public sealed interface HoldingOperation extends AccountOperation permits Buy, Dividend, Sell, Transaction {
    String holdingSymbol();

    default String getAccountWithSymbol() {
        return this.account() + ":" + this.holdingSymbol();
    }
}
