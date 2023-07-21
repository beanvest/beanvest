package beanvest.test.tradingjournal.model.entry;

import beanvest.test.tradingjournal.model.Value;

import java.util.Optional;

public sealed interface Transfer extends CashOperation permits Deposit, Dividend, Fee, Interest, Withdrawal {
    Value value();

    Optional<String> comment();
}
