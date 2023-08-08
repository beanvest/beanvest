package beanvest.journal.entry;

import beanvest.journal.Value;
import beanvest.journal.entity.Account2;

import java.math.BigDecimal;
import java.util.Optional;

public sealed interface Transaction extends CashOperation, HoldingOperation, HasRawAmountMoved permits Buy, Sell {

    String holdingSymbol();
    Account2 account2();

    BigDecimal units();

    Value totalPrice();

    Optional<String> comment();

    BigDecimal fee();
}
