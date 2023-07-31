package beanvest.processor.processing.calculator;

import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AccountValueCalculator {
    private final HoldingsValueCalculator holdingsValueCalculator;
    private final CashCalculator cashCalculator;

    public AccountValueCalculator(HoldingsValueCalculator holdingsCostCalculator, CashCalculator cashCalculator) {
        holdingsValueCalculator = holdingsCostCalculator;
        this.cashCalculator = cashCalculator;
    }
    public Result<BigDecimal, UserErrors> calculate(LocalDate endingDate, String targetCurrency)
    {
        return holdingsValueCalculator.calculate(endingDate, targetCurrency)
                .combine(cashCalculator.calculate(), BigDecimal::add, UserErrors::join);
    }
}
