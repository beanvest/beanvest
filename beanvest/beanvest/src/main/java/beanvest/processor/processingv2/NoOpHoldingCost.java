package beanvest.processor.processingv2;

import beanvest.journal.Value;

import java.math.BigDecimal;

public class NoOpHoldingCost implements HoldingCost {
    @Override
    public void updateAvgCost(BigDecimal newAmount) {

    }

    @Override
    public Value totalCost() {
        return null;
    }

    @Override
    public void bumpCostWhileKeepingAvg(BigDecimal keptRatio) {

    }

    @Override
    public void add(BigDecimal newCost, BigDecimal newAmount) {

    }

    @Override
    public void setTotalCost(BigDecimal newTotalCost, BigDecimal newAmount) {

    }

    @Override
    public Value lastAvgCost() {
        return null;
    }
}
