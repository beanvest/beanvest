package beanvest.journal.entry;

import beanvest.journal.entity.Account2;
import beanvest.journal.entity.Group;

import java.time.LocalDate;

public sealed interface AccountOperation extends Entry permits Balance, CashOperation, Close, HoldingOperation, DepositOrWithdrawal {

    LocalDate date();

    //use Account objects instead
    @Deprecated()
    default String account()
    {
        return account2().nameWithGroup();
    }

    Account2 account2();
    default Group group()
    {
        return account2().group();
    }
}
