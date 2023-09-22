package beanvest.processor.processingv2;

import beanvest.journal.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Holding {
    private static final int DEFAULT_SCALE = 6;
    private final String symbol;
    private BigDecimal amount;
    private BigDecimal totalCost;
    private BigDecimal lastAvgCost;

    public Holding(String symbol, BigDecimal amount, BigDecimal totalCost) {
        amount = amount.setScale(DEFAULT_SCALE, RoundingMode.HALF_UP);
        totalCost = totalCost.setScale(DEFAULT_SCALE, RoundingMode.HALF_UP);
        this.symbol = symbol;
        this.amount = amount;
        this.totalCost = totalCost;
        updateAvgCost();
    }

    public void update(BigDecimal amountChange, BigDecimal newCost) {
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            amount = amountChange;
            totalCost = newCost;
        } else if (isIncreasingHolding(amountChange)) {
            updateAmountAndAvgCost(amountChange, newCost);
        } else {
            if (willCrossZero(amountChange)) { // goes over 0
                var negatedAmount = amount.negate();
                var splitRatio = negatedAmount.divide(amountChange, DEFAULT_SCALE*2, RoundingMode.DOWN);
                var costToZero = splitRatio.multiply(newCost).setScale(DEFAULT_SCALE, RoundingMode.HALF_UP);
                update(negatedAmount, costToZero);
                var remainingAmount = amountChange.subtract(negatedAmount);
                update(remainingAmount, newCost.subtract(costToZero));
                return;
            }
            updateAmountWhileKeepingAvgCost(amountChange);
        }
        updateAvgCost();
    }

    private boolean willCrossZero(BigDecimal amountChange) {
        var newSideOfZero = amount.add(amountChange).compareTo(BigDecimal.ZERO);
        var currentSideOfZero = amount.compareTo(BigDecimal.ZERO);
        return newSideOfZero == -currentSideOfZero;
    }

    private void updateAmountAndAvgCost(BigDecimal amountChange, BigDecimal newCost) {
        amount = amount.add(amountChange);
        totalCost = totalCost.add(newCost);
    }

    private boolean isIncreasingHolding(BigDecimal amountChange) {
        var isAmountPositive = this.amount.compareTo(BigDecimal.ZERO) > 0;
        var isChangePositive = amountChange.compareTo(BigDecimal.ZERO) > 0;
        return isAmountPositive == isChangePositive;
    }

    private void updateAvgCost() {
        if (amount.compareTo(BigDecimal.ZERO) != 0) {
            lastAvgCost = totalCost.divide(amount, DEFAULT_SCALE, RoundingMode.HALF_UP);
        }
    }

    public BigDecimal averageCost() {
        return lastAvgCost;
    }

    private void updateAmountWhileKeepingAvgCost(BigDecimal amountChange) {
        var ratio = amountChange.negate().divide(amount, 10, RoundingMode.DOWN);
        var keptRatio = BigDecimal.ONE.subtract(ratio);
        this.amount = amount.add(amountChange);
        this.totalCost = this.totalCost.multiply(keptRatio).setScale(DEFAULT_SCALE, RoundingMode.HALF_UP);
    }

    public Value asValue() {
        return Value.of(amount, symbol);
    }

    public BigDecimal totalCost() {
        return totalCost;
    }

    public String symbol() {
        return symbol;
    }

    public BigDecimal amount() {
        return amount;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Holding) obj;
        return Objects.equals(this.symbol, that.symbol) &&
               Objects.equals(this.amount, that.amount) &&
               Objects.equals(this.totalCost, that.totalCost);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, amount, totalCost);
    }


    @Override
    public String toString() {
        return "Holding[" +
               "symbol=" + symbol + ", " +
               "amount=" + amount + ", " +
               "totalCost=" + totalCost + ']';
    }

    public void updateWhileKeepingTheCost(BigDecimal d) {
        amount = amount.add(d);
        updateAvgCost();
    }
}
