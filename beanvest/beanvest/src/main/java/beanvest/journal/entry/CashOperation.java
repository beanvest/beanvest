package beanvest.journal.entry;

import beanvest.journal.Value;
import beanvest.journal.entity.AccountCashHolding;

import java.math.BigDecimal;

public sealed interface CashOperation extends AccountOperation permits Transaction, Transfer {
    Value getCashValue();

    default Value getCashValueConverted() {
        return this.getCashValue().originalValue().get();
    }
    default BigDecimal getCashAmount() {
        return getCashValue().amount();
    }

    default String getCashCurrency() {
        return getCashValue().symbol();
    }

    default AccountCashHolding accountCash()
    {
        return this.account().cashHolding(getCashCurrency());
    }
}
