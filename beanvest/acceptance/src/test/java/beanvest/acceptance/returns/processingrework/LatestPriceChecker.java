package beanvest.acceptance.returns.processingrework;

import beanvest.journal.entry.Entry;
import beanvest.processor.processing.AccountType;
import beanvest.result.ErrorFactory;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.time.LocalDate;

class LatestPriceChecker implements Processor, Calculator {
    private final AccountsResolver2 accountsResolver;
    private int retuned = 0;

    public LatestPriceChecker(AccountsResolver2 accountsResolver) {
        this.accountsResolver = accountsResolver;
    }

    @Override
    public void process(Entry entry) {
    }

    @Override
    public Result<BigDecimal, UserErrors> calculate(String account, LocalDate endDate, String targetCurrency) {
        var acc = accountsResolver.findKnownAccount(account).get();

        if (acc.type() == AccountType.HOLDING) {
            retuned += 1;
            return Result.success(new BigDecimal(retuned * 10));
        } else {
            return Result.failure(ErrorFactory.disabledForAccountType());
        }
    }
}
