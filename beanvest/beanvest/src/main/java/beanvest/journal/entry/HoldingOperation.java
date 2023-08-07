package beanvest.journal.entry;

import beanvest.processor.processingv2.AccountHolding;

public sealed interface HoldingOperation extends AccountOperation permits Buy, Dividend, Sell, Transaction {
    String holdingSymbol();

    default String getAccountWithSymbol() {
        return this.account() + ":" + this.holdingSymbol();
    }

    default AccountHolding accountHolding() {
        return new AccountHolding(account2(), holdingSymbol());
    }
}
