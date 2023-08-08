package beanvest.processor.processingv2;

import beanvest.journal.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Holding(String symbol, BigDecimal amount, BigDecimal unitCost) {
    private static final int DEFAULT_SCALE = 6;

    public Holding {
        amount = amount.setScale(DEFAULT_SCALE, RoundingMode.HALF_UP);
        unitCost = unitCost.setScale(DEFAULT_SCALE, RoundingMode.HALF_UP);
    }

    public Holding addBought(BigDecimal boughtAmount, BigDecimal boughtTotalCost) {
        var newTotalAmount = amount.add(boughtAmount);
        var newUnitCost = amount.multiply(unitCost)
                .add(boughtTotalCost)
                .divide(newTotalAmount, RoundingMode.HALF_UP);
        return new Holding(symbol, newTotalAmount, newUnitCost);
    }

    public BigDecimal averageCost() {
        return unitCost;
    }

    public Holding reduceSold(BigDecimal soldAmount) {
        var newAmount = amount.subtract(soldAmount);
        return new Holding(symbol, newAmount, unitCost);
    }

    public Value asValue() {
        return Value.of(amount, symbol);
    }

    public BigDecimal totalCost() {
        return amount.multiply(unitCost);
    }

    public String toShortString()
    {
        return amount.stripTrailingZeros().toPlainString() + " " + symbol;
    }
}
