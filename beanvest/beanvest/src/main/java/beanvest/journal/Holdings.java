package beanvest.journal;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Holdings {
    final Map<String, Holding> holdings;

    public Holdings() {
        this.holdings = new HashMap<>();
    }

    public Holdings(Map<String, Holding> holdings) {
        this.holdings = holdings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Holdings holdings = (Holdings) o;
        return this.holdings.equals(holdings.holdings);
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "holdings=" + holdings +
                '}';
    }

    public BigDecimal getTotalCost() {
        return holdings.values().stream()
                .map(Holding::totalPrice)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    public Holdings copy() {
        return new Holdings(new HashMap<>(holdings));
    }

    public Holdings add(Holdings b) {
        var copy = this.copy();
        b.holdings.forEach((key, value) -> copy.buy(key, value.value().amount(), value.totalPrice(), BigDecimal.ZERO));
        return copy;
    }

    /**
     * @return BigDecimal realized gain
     */
    public BigDecimal sell(String symbol, BigDecimal soldAmount, BigDecimal totalPrice) {
        var totalPurchasePriceBasedOnAverage = holdings.get(symbol).averagePrice().multiply(soldAmount);
        var orDefault = holdings.getOrDefault(symbol, Holding.ZERO);
        var newHolding = orDefault
                .reduceSold(soldAmount);
        if (newHolding.value().amount().compareTo(BigDecimal.ZERO) == 0) {
            holdings.remove(symbol);
        } else {
            holdings.put(symbol, newHolding);
        }

        var subtract = totalPrice.subtract(totalPurchasePriceBasedOnAverage);
        return subtract;
    }

    public void buy(String symbol, BigDecimal amount, BigDecimal totalPrice, BigDecimal fee) {
        var orDefault = holdings.getOrDefault(symbol, Holding.ZERO);
        var newHolding = orDefault
                .addBought(Value.of(amount, symbol), totalPrice);
        if (newHolding.value().amount().equals(BigDecimal.ZERO)) {
            holdings.remove(symbol);
        } else {
            holdings.put(symbol, newHolding);
        }
    }

    public Holding get(String symbol) {
        return holdings.getOrDefault(symbol, Holding.ZERO);
    }

    public List<Value> asList() {
        return holdings.values().stream()
                .map(Holding::value)
                .sorted(Comparator.comparing(Value::symbol))
                .toList();
    }

    public Map<String, Holding> asMap() {
        return new HashMap<>(holdings);
    }

    public boolean isEmpty() {
        return holdings.isEmpty();
    }
}
