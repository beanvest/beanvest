package beanvest.processor.processingv2;

import beanvest.journal.Value;

import java.math.BigDecimal;

public interface HoldingCost {
    public final static HoldingCost NO_OP = new NoOpHoldingCost();

    void updateAvgCost(BigDecimal newAmount);

    Value totalCost();

    void bumpCostWhileKeepingAvg(BigDecimal keptRatio);

    void add(BigDecimal newCost, BigDecimal newAmount);

    void setTotalCost(BigDecimal newTotalCost, BigDecimal newAmount);

    Value lastAvgCost();

}
