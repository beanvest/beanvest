package beanvest.processor.processing.calculator;

import beanvest.processor.processing.collector.DepositCollector;
import beanvest.processor.processing.collector.WithdrawalCollector;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
        var calculate = accountValueCalculator.calculate(date, currency);
        var balance = depositCollector.balance();
        var withdrawalCollector1 = withdrawalCollector.balance();
        return Result.combine(List.of(calculate, balance, withdrawalCollector1), BigDecimal::subtract, UserErrors::join);
    }
}
