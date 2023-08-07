package beanvest.journal.entry;

import beanvest.journal.Value;
import beanvest.processor.processingv2.Account2;
import beanvest.processor.processingv2.AccountHolding;
import beanvest.processor.processingv2.Entity;

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
