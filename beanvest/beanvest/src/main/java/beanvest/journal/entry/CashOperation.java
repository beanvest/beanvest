package beanvest.journal.entry;

import beanvest.journal.entity.Entity;

import java.math.BigDecimal;

public sealed interface CashOperation extends AccountOperation permits Transaction, Transfer {
    BigDecimal getCashAmount();

    String getCashCurrency();

    default Entity cashAccount()
    {
        return this.account2().cashHolding();
    }
}
