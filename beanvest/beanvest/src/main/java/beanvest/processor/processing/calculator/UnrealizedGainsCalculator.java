package beanvest.processor.processing.calculator;

import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UnrealizedGainsCalculator {
    private final HoldingsValueCalculator holdingsValueCalculator;
    private final HoldingsCostCalculator holdingsCostCalculator;

    public UnrealizedGainsCalculator(HoldingsValueCalculator holdingsValueCalculator, HoldingsCostCalculator holdingsCostCalculator) {

        this.holdingsValueCalculator = holdingsValueCalculator;
        this.holdingsCostCalculator = holdingsCostCalculator;
    }
    public Result<BigDecimal, UserErrors> calculate(LocalDate endingDate, String targetCurrency)
    {
        var holdingsValue = holdingsValueCalculator.calculateValue(endingDate, targetCurrency);
        return holdingsValue.map(value -> value.subtract(holdingsCostCalculator.get()));
    }
}
