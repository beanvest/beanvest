package beanvest.journal;

import beanvest.parser.ValueFormatException;

import java.math.BigDecimal;

public record Value(BigDecimal amount, String symbol) {
    public static final Value ZERO = new Value(BigDecimal.ZERO, "");

    public static Value of(String value, String symbol) throws ValueFormatException {
        return new Value(new BigDecimal(value), symbol);
    }

    public static Value of(BigDecimal value, String symbol) throws ValueFormatException {
        return new Value(value, symbol);
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

    public String getSymbol() {
        return symbol;
    }

    public Value add(Value value) {
        verifySameSymbol(value);
        return new Value(value.getAmount().add(this.amount), this.amount.equals(BigDecimal.ZERO) ? value.getSymbol() : this.symbol);
    }

    public Value add(BigDecimal value) {
        return new Value(this.amount.add(value), this.symbol);
    }

    public Value subtract(Value value) {
        verifySameSymbol(value);
        return this.add(value.negate());
    }

    public Value negate() {
        return new Value(this.getAmount().negate(), this.symbol);
    }

    public String toString() {
        return this.amount.toString() + " " + this.symbol;
    }

    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public Value abs() {
        return isPositive() ? this : this.negate();
    }

    private void verifySameSymbol(Value value) {
        if (this.amount.equals(BigDecimal.ZERO) || value.amount.equals(BigDecimal.ZERO)) {
            return;
        }
        if (!this.symbol.equals(value.getSymbol())) {
            throw new ArithmeticException(String.format("cant operate on different commodities: %s and %s", this.symbol, value.getSymbol()));
        }
    }
}
