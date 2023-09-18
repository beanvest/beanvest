package beanvest.journal.entry;

import beanvest.journal.Value;
import beanvest.journal.entity.Account2;
import beanvest.journal.entity.AccountInstrumentHolding;

import java.math.BigDecimal;
import java.util.Optional;

public sealed interface Transaction extends CashOperation, HoldingOperation, HasRawAmountMoved permits Buy, Sell {

    String holdingSymbol();

    Account2 account();

    BigDecimal units();

    Value totalPrice();

    Optional<String> comment();

    BigDecimal fee();

    default AccountInstrumentHolding getInstrumentHolding() {
        return new AccountInstrumentHolding(account(), this.holdingSymbol());
    }

    Transaction withValue(Value newValue);
}
