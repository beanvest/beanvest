package beanvest.journal.entry;

import beanvest.journal.entity.AccountCashHolding;

import java.math.BigDecimal;

public sealed interface CashOperation extends AccountOperation permits Transaction, Transfer {
    BigDecimal getCashAmount();

    String getCashCurrency();

    default AccountCashHolding cashAccount()
    {
        return this.account().cashHolding(getCashCurrency());
    }
}
