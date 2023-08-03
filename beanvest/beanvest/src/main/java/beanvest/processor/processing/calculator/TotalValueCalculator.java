package beanvest.processor.processing.calculator;

import beanvest.journal.entry.Entry;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TotalValueCalculator implements StatCalculator {
    private final HoldingsValueCalculator holdingsValueCalculator;
    private final CashCalculator cashCalculator;

    public TotalValueCalculator(HoldingsValueCalculator holdingsValueCalculator, CashCalculator cashCalculator) {
        this.holdingsValueCalculator = holdingsValueCalculator;
        this.cashCalculator = cashCalculator;
    }
    
    public Result<BigDecimal, UserErrors> calculate(LocalDate endingDate, String targetCurrency)
    {
        return holdingsValueCalculator.calculate(endingDate, targetCurrency)
                        .combine(cashCalculator.calculate(), BigDecimal::add, UserErrors::join);
    }

    @Override
    public void process(Entry entry) {
    }
}
