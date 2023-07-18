package beanvest.tradingjournal.model.entry;

import beanvest.tradingjournal.model.Value;

import java.math.BigDecimal;
import java.util.Optional;

public sealed interface Transaction extends CashOperation permits Buy, Sell {
    String commodity();

    BigDecimal units();

    Value totalPrice();

    Optional<String> comment();

    BigDecimal fee();
}
