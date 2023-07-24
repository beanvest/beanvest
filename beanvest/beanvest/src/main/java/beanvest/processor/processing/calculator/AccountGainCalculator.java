package beanvest.processor.processing.calculator;

import beanvest.processor.processing.collector.DepositCollector;
import beanvest.processor.processing.collector.WithdrawalCollector;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AccountGainCalculator {
    private final DepositCollector depositCollector;
    private final WithdrawalCollector withdrawalCollector;
    private final AccountValueCalculator accountValueCalculator;

    public AccountGainCalculator(DepositCollector depositCollector, WithdrawalCollector withdrawalCollector, AccountValueCalculator accountValueCalculator) {

        this.depositCollector = depositCollector;
        this.withdrawalCollector = withdrawalCollector;
        this.accountValueCalculator = accountValueCalculator;
    }

    public Result<BigDecimal, UserErrors> calculate(LocalDate date, String currency) {
        return accountValueCalculator.calculate(date, currency)
                .map(accountValue -> {
                    var deposits = depositCollector.balance();
                    var withdrawals = withdrawalCollector.balance();
                    var dw = deposits.add(withdrawals);
                    return accountValue
                            .subtract(dw);
                });
    }
}
