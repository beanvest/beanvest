package beanvest.journal.entry;

import java.time.LocalDate;

public sealed interface AccountOperation extends Entry permits Balance, CashOperation, Close, HoldingOperation, DepositOrWithdrawal {

    LocalDate date();

    String account();
}
