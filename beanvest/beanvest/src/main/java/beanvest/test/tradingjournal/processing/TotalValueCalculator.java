package beanvest.test.tradingjournal.processing;

import beanvest.test.tradingjournal.Result;
import beanvest.test.tradingjournal.model.UserErrors;
import beanvest.test.tradingjournal.model.Value;
import beanvest.test.tradingjournal.processing.collector.CashCalculator;
import beanvest.test.tradingjournal.processing.calculator.HoldingsValueCalculator;

import java.time.LocalDate;

public class TotalValueCalculator {
    private final HoldingsValueCalculator holdingsValueCalculator;
    private final CashCalculator cashCalculator;

    public TotalValueCalculator(HoldingsValueCalculator holdingsValueCalculator, CashCalculator cashCalculator) {
        this.holdingsValueCalculator = holdingsValueCalculator;
        this.cashCalculator = cashCalculator;
    }
    
    public Result<Value, UserErrors> calculateValue(LocalDate endingDate, String targetCurrency)
    {
        return
                holdingsValueCalculator.calculateValue(endingDate, targetCurrency)
                        .map(v -> v.add(cashCalculator.balance()));
    }
}
