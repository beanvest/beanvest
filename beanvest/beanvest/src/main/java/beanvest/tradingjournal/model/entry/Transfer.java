package beanvest.tradingjournal.model.entry;

import beanvest.tradingjournal.model.Value;

import java.util.Optional;

public sealed interface Transfer extends CashOperation permits Deposit, Dividend, Fee, Interest, Withdrawal {
    Value value();

    Optional<String> comment();
}
