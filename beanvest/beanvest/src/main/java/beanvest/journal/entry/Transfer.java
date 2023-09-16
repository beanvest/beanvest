package beanvest.journal.entry;

import beanvest.journal.Value;

import java.util.Optional;

public sealed interface Transfer extends CashOperation, HasRawAmountMoved permits Deposit, Dividend, Fee, Interest, Withdrawal {
    Value value();

    Optional<String> comment();

    Transfer withCashValue(Value value);
}
