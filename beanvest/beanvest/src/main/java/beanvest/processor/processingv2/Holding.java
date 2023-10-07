package beanvest.processor.processingv2;

import beanvest.journal.Value;
import beanvest.journal.entity.AccountHolding;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Holding {
    private static final int DEFAULT_SCALE = 6;
    private static final int EXTRA_SCALE = 4;
    private final String symbol;
    private BigDecimal amount;
    private final Cost cost;
    private final String costSymbol;
    private int givenCostScale;

    public Holding(String symbol, BigDecimal amount, Value totalCost) {
        costSymbol = totalCost.symbol();
        this.symbol = symbol;
        this.amount = amount;
        this.cost = new Cost(amount, totalCost.amount().setScale(DEFAULT_SCALE, RoundingMode.HALF_UP));
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
        if (!newCost.symbol().equals(costSymbol)) {
            throw new RuntimeException("tried to add cost in `"+newCost.symbol()+"` to `"+costSymbol+"`");
        }
        cost.update(amount, amountChange, newCost.amount());
        amount = amount.add(amountChange);
    }

    public void updateWhileKeepingTheCost(BigDecimal d) {
        amount = amount.add(d);
    }

    public Value averageCost() {
        return Value.of(cost.avgCost(amount), costSymbol);
    }

    public Value totalCost() {
        return Value.of(cost.totalCost(), costSymbol);
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

    public Value asValue() {
        return Value.of(amount, symbol);
    }
}
