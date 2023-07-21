package beanvest.test.tradingjournal.model.entry;

import java.time.LocalDate;

public sealed interface AccountOperation extends Entry permits Balance, CashOperation, Close, DepositOrWithdrawal {

    LocalDate date();

    String account();
}
