package beanvest.test.tradingjournal.model;

import beanvest.test.tradingjournal.ValueFormatException;

import java.math.BigDecimal;

public record Value(BigDecimal amount, String commodity) {
    public static final Value ZERO = new Value(BigDecimal.ZERO, "");

    public static Value of(String value, String commodity) throws ValueFormatException {
        return new Value(new BigDecimal(value), commodity);
    }

    public static Value of(BigDecimal value, String commodity) throws ValueFormatException {
        return new Value(value, commodity);
    }

    public static Value of(String valueString) throws ValueFormatException {
        try {
            var parts = valueString.strip().split("\\s+");
            var amount = new BigDecimal(parts[0]);
            return new Value(amount, parts[1]);

        } catch (NumberFormatException e) {
            throw new ValueFormatException("Invalid Value given: " + valueString);
        }
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCommodity() {
        return commodity;
    }

    public Value add(Value value) {
        verifySameCommodity(value);
        return new Value(value.getAmount().add(this.amount), this.amount.equals(BigDecimal.ZERO) ? value.getCommodity() : this.commodity);
    }

    public Value add(BigDecimal value) {
        return new Value(this.amount.add(value), this.commodity);
    }

    public Value subtract(Value value) {
        verifySameCommodity(value);
        return this.add(value.negate());
    }

    public Value negate() {
        return new Value(this.getAmount().negate(), this.commodity);
    }

    public String toString() {
        return this.amount.toString() + " " + this.commodity;
    }

    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public Value abs() {
        return isPositive() ? this : this.negate();
    }

    private void verifySameCommodity(Value value) {
        if (this.amount.equals(BigDecimal.ZERO) || value.amount.equals(BigDecimal.ZERO)) {
            return;
        }
        if (!this.commodity.equals(value.getCommodity())) {
            throw new ArithmeticException(String.format("cant operate on different commodities: %s and %s", this.commodity, value.getCommodity()));
        }
    }
}
