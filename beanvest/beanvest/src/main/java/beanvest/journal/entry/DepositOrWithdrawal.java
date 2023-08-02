package beanvest.journal.entry;

public sealed interface DepositOrWithdrawal extends AccountOperation, HasRawAmountMoved permits Deposit, Withdrawal {
}
