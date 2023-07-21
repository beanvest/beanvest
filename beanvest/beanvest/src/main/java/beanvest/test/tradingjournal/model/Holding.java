package beanvest.test.tradingjournal.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Holding(Value value, BigDecimal totalPrice) {
    public static final Holding ZERO = new Holding(Value.ZERO, BigDecimal.ZERO);

    public Holding addBought(Value value, BigDecimal totalPrice) {
        return new Holding(this.value.add(value), this.totalPrice.add(totalPrice));
    }

    public Holding reduceSold(BigDecimal amount) {
        var avgPrice = this.averagePrice();
        var newTotalPrice = totalPrice.subtract(avgPrice.multiply(amount));
        var newAmount = this.value.add(amount.negate());
        return new Holding(newAmount, newTotalPrice);
    }

    public BigDecimal averagePrice() {
        return totalPrice.divide(value.amount(), 5, RoundingMode.HALF_UP);
    }

    public BigDecimal units() {
        return value.amount();
    }
}
