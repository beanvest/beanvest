package beanvest.journal.entry;

import beanvest.journal.Value;

import java.math.BigDecimal;
import java.util.Optional;

public sealed interface Transaction extends CashOperation permits Buy, Sell {
    String holdingSymbol();

    BigDecimal units();

    Value totalPrice();

    Optional<String> comment();

    BigDecimal fee();
}
