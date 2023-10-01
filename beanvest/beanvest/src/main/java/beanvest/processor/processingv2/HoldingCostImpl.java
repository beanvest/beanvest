package beanvest.processor.processingv2;

import beanvest.journal.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static beanvest.processor.processingv2.Holding.DEFAULT_SCALE;

public class HoldingCostImpl implements HoldingCost {
    private final String currency;
    private BigDecimal totalCost;
    private BigDecimal lastAvgCost;

    public HoldingCostImpl(String currency) {
        this.currency = currency;
    }

    @Override
    public void updateAvgCost(BigDecimal newAmount) {
        if (newAmount.compareTo(BigDecimal.ZERO) != 0) {
            lastAvgCost = totalCost.divide(newAmount, DEFAULT_SCALE, RoundingMode.HALF_UP);
        }
    }

    @Override
    public Value totalCost() {
        return Value.of(totalCost, currency);
    }

    @Override
    public void bumpCostWhileKeepingAvg(BigDecimal keptRatio) {
        this.totalCost = this.totalCost.multiply(keptRatio)
                .setScale(DEFAULT_SCALE, RoundingMode.HALF_UP);
    }

    @Override
    public void add(BigDecimal newCost, BigDecimal newAmount) {
        totalCost = totalCost.add(newCost);
        updateAvgCost(newAmount);
    }

    @Override
    public void setTotalCost(BigDecimal newTotalCost, BigDecimal newAmount) {
        totalCost = newTotalCost.setScale(DEFAULT_SCALE, RoundingMode.HALF_UP);
        updateAvgCost(newAmount);
    }

    @Override
    public Value lastAvgCost() {
        return Value.of(lastAvgCost, currency);
    }

    @Override
    public String toString() {
        return currency + " totalCost: " + totalCost +
                " avgCost: " + lastAvgCost;
    }
}