package beanvest.processor.processingv2;

import beanvest.journal.Value;
import beanvest.journal.entity.AccountHolding;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class HoldingWithCost {
    private static final int DEFAULT_SCALE = 6;
    private final String symbol;
    private BigDecimal amount;
    private final Cost cost;

    public HoldingWithCost(String symbol, BigDecimal amount, BigDecimal totalCost) {
        amount = amount.setScale(DEFAULT_SCALE, RoundingMode.HALF_UP);
        totalCost = totalCost.setScale(DEFAULT_SCALE, RoundingMode.HALF_UP);
        this.symbol = symbol;
        this.amount = amount;
        this.cost = new Cost(amount, totalCost);
    }

    public void update(BigDecimal amountChange, BigDecimal newCost) {
        cost.update(amount, amountChange, newCost);
        amount = amount.add(amountChange);
    }

    static HoldingWithCost getHoldingOrCreate(HoldingWithCost v, AccountHolding accountHolding, Value cashValue, BigDecimal totalCost) {
        if (v == null) {
            return new HoldingWithCost(accountHolding.symbol(), cashValue.amount(), totalCost);
        } else {
            v.update(cashValue.amount(), totalCost);
            return v;
        }
    }


    public BigDecimal averageCost() {
        return cost.avgCost(amount);
    }


    public Value asValue() {
        return Value.of(amount, symbol);
    }

    public BigDecimal totalCost() {
        return cost.totalCost();
    }

    public String symbol() {
        return symbol;
    }

    public BigDecimal amount() {
        return amount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, amount, totalCost());
    }


    @Override
    public String toString() {
        return "Holding[" +
                "symbol=" + symbol + ", " +
                "amount=" + amount + ", " +
                "cost=" + cost + ']';
    }

    public void updateWhileKeepingTheCost(BigDecimal d) {
        amount = amount.add(d);
    }
}
