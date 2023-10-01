package beanvest.processor.processingv2;

import java.math.BigDecimal;
import java.math.RoundingMode;

class Cost {
    private static final int DEFAULT_SCALE = 6;

    private BigDecimal totalCost;

    private BigDecimal lastAvgCost;
    Cost(BigDecimal amount, BigDecimal totalCost) {
        this.totalCost = totalCost;
        updateAvgCost(amount);
    }

    private void updateAvgCost(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) != 0) {
            lastAvgCost = calculateAverage(amount, totalCost);
        }
    }

    private BigDecimal calculateAverage(BigDecimal amount, BigDecimal totalCost) {
        return totalCost.divide(amount, DEFAULT_SCALE, RoundingMode.HALF_UP);
    }

    public void update(BigDecimal oldAmount, BigDecimal amountChange, BigDecimal newCost) {
        if (oldAmount.compareTo(BigDecimal.ZERO) == 0) {
            totalCost = newCost;
        } else if (isIncreasingHolding(oldAmount, amountChange)) {
            totalCost = totalCost.add(newCost);
        } else {
            if (willCrossZero(oldAmount, amountChange)) {
                var negatedAmount = oldAmount.negate();
                var splitRatio = negatedAmount.divide(amountChange, DEFAULT_SCALE * 2, RoundingMode.DOWN);
                var costToZero = splitRatio.multiply(newCost).setScale(DEFAULT_SCALE, RoundingMode.HALF_UP);
                totalCost = newCost.subtract(costToZero);
            } else {
                updateAmountWhileKeepingAvgCost(oldAmount, amountChange);
            }
        }
        updateAvgCost(oldAmount.add(amountChange));
    }

    private void updateAmountWhileKeepingAvgCost(BigDecimal oldAmount, BigDecimal amountChange) {
        var ratio = amountChange.negate().divide(oldAmount, 10, RoundingMode.DOWN);
        var keptRatio = BigDecimal.ONE.subtract(ratio);
        this.totalCost = this.totalCost.multiply(keptRatio).setScale(DEFAULT_SCALE, RoundingMode.HALF_UP);
    }

    private boolean isIncreasingHolding(BigDecimal oldAmount, BigDecimal amountChange) {
        var isAmountPositive = oldAmount.compareTo(BigDecimal.ZERO) > 0;
        var isChangePositive = amountChange.compareTo(BigDecimal.ZERO) > 0;
        return isAmountPositive == isChangePositive;
    }

    private boolean willCrossZero(BigDecimal oldAmount, BigDecimal amountChange) {
        var newSideOfZero = oldAmount.add(amountChange).compareTo(BigDecimal.ZERO);
        var currentSideOfZero = oldAmount.compareTo(BigDecimal.ZERO);
        return newSideOfZero == -currentSideOfZero;
    }

    public BigDecimal avgCost(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) != 0) {
            return calculateAverage(amount, totalCost);
        } else {
            return lastAvgCost;
        }
    }

    public BigDecimal totalCost() {
        return totalCost;
    }
}
