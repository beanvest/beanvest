package beanvest.processor.processingv2;

import beanvest.journal.Value;
import beanvest.journal.entity.AccountHolding;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;

public final class Holding {
    static final int DEFAULT_SCALE = 6;
    private final String symbol;
    private final HoldingCostImpl holdingCost;
    private HoldingCost convertedValue;
    private BigDecimal amount;

    public Holding(String symbol, BigDecimal amount, Value totalCost) {
        amount = amount.setScale(DEFAULT_SCALE, RoundingMode.HALF_UP);
        this.symbol = symbol;
        this.amount = amount;
        holdingCost = new HoldingCostImpl(totalCost.symbol());
        this.holdingCost.setTotalCost(totalCost.amount(), amount);

        if (totalCost.convertedValue().isEmpty()) {
            convertedValue = HoldingCost.NO_OP;
        } else {
            var value = totalCost.convertedValue().get();
            convertedValue = new HoldingCostImpl(value.symbol());
            this.convertedValue.setTotalCost(totalCost.convertedValue().get().amount(), amount);
        }
    }

    static Holding getHoldingOrCreate(Holding v, AccountHolding accountHolding, Value cashValue, Value totalCost) {
        if (v == null) {
            return new Holding(accountHolding.symbol(), cashValue.amount(), totalCost);
        } else {
            v.update(cashValue.amount(), totalCost);
            return v;
        }
    }

    public void update(BigDecimal amountChange, Value newCost) {
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            amount = amountChange;
            holdingCost.setTotalCost(newCost.amount(), amount);
            newCost.convertedValue().ifPresent(oc -> convertedValue = new HoldingCostImpl(oc.symbol()));
            convertedValue.setTotalCost(getConvertedValueOrDefault(newCost), amount);
        } else if (isIncreasingHolding(amountChange)) {
            updateAmountAndAvgCost(amountChange, newCost);
        } else {
            if (willCrossZero(amountChange)) { // goes over 0
                var negatedAmount = amount.negate();
                var splitRatio = negatedAmount.divide(amountChange, DEFAULT_SCALE * 2, RoundingMode.DOWN);
                var costToZero = Value.of(newCost.multiply(splitRatio).amount().setScale(DEFAULT_SCALE, RoundingMode.HALF_UP), newCost.symbol());
                var maybeCostToZeroOC = newCost.convertedValue()
                        .map(vOC -> Value.of(splitRatio.multiply(vOC.amount())
                                .setScale(DEFAULT_SCALE, RoundingMode.HALF_UP), symbol));
                var costToZeroWithOriginalValue = new Value(costToZero, maybeCostToZeroOC);
                update(negatedAmount, costToZeroWithOriginalValue);
                var remainingAmount = amountChange.subtract(negatedAmount);
                update(remainingAmount, newCost.subtract(costToZeroWithOriginalValue));
                return;
            }
            updateAmountWhileKeepingAvgCost(amountChange);
        }
    }

    private static BigDecimal getConvertedValueOrDefault(Value newCost) {
        return newCost.convertedValue().map(Value::amount).orElse(BigDecimal.ZERO);
    }

    private boolean willCrossZero(BigDecimal amountChange) {
        var newSideOfZero = amount.add(amountChange).compareTo(BigDecimal.ZERO);
        var currentSideOfZero = amount.compareTo(BigDecimal.ZERO);
        return newSideOfZero == -currentSideOfZero;
    }

    private void updateAmountAndAvgCost(BigDecimal amountChange, Value newCost) {
        amount = amount.add(amountChange);
        holdingCost.add(newCost.amount(), amount);
        convertedValue.add(getConvertedValueOrDefault(newCost), amount);
    }

    private boolean isIncreasingHolding(BigDecimal amountChange) {
        var isAmountPositive = this.amount.compareTo(BigDecimal.ZERO) > 0;
        var isChangePositive = amountChange.compareTo(BigDecimal.ZERO) > 0;

        return isAmountPositive == isChangePositive;
    }

    public Value averageCost() {
        return new Value(holdingCost.lastAvgCost(), Optional.ofNullable(convertedValue.lastAvgCost()));
    }

    private void updateAmountWhileKeepingAvgCost(BigDecimal amountChange) {
        var ratio = amountChange.negate().divide(amount, 10, RoundingMode.DOWN);
        var keptRatio = BigDecimal.ONE.subtract(ratio);
        this.amount = amount.add(amountChange);
        this.holdingCost.bumpCostWhileKeepingAvg(keptRatio);
        this.convertedValue.bumpCostWhileKeepingAvg(keptRatio);
    }

    public Value asValue() {
        return Value.of(amount, symbol);
    }

    public Value totalCost() {
        return new Value(holdingCost.totalCost(), Optional.ofNullable(convertedValue.totalCost()));
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
                Objects.equals(this.holdingCost.totalCost(), that.holdingCost.totalCost());
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, amount, holdingCost.totalCost());
    }


    public void updateWhileKeepingTheCost(BigDecimal d) {
        amount = amount.add(d);
        holdingCost.updateAvgCost(amount);
    }
}
