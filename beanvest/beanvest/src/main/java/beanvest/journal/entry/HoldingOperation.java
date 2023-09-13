package beanvest.journal.entry;

import beanvest.journal.entity.AccountInstrumentHolding;

public sealed interface HoldingOperation extends AccountOperation permits Buy, Dividend, Sell, Transaction {
    String holdingSymbol();

    default String getAccountWithSymbol() {
        return this.aNameWithGroupOrSomething() + ":" + this.holdingSymbol();
    }

    default AccountInstrumentHolding accountHolding() {
        return new AccountInstrumentHolding(account(), holdingSymbol());
    }
}
