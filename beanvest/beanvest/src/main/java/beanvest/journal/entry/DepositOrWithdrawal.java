package beanvest.journal.entry;

import java.math.BigDecimal;

public sealed interface DepositOrWithdrawal extends AccountOperation permits Deposit, Withdrawal {
    BigDecimal getRawAmountMoved();
}
