package beanvest.processor.processing.calculator;

import beanvest.result.Result;
import beanvest.result.UserErrors;
import beanvest.journal.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TotalValueCalculator {
    private final HoldingsValueCalculator holdingsValueCalculator;
    private final CashCalculator cashCalculator;

    public TotalValueCalculator(HoldingsValueCalculator holdingsValueCalculator, CashCalculator cashCalculator) {
        this.holdingsValueCalculator = holdingsValueCalculator;
        this.cashCalculator = cashCalculator;
    }
    
    public Result<BigDecimal, UserErrors> calculateValue(LocalDate endingDate, String targetCurrency)
    {
        return
                holdingsValueCalculator.calculateValue(endingDate, targetCurrency)
                        .map(v -> v.add(cashCalculator.balance()));
    }
}
