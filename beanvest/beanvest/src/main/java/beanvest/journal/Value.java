package beanvest.journal;

import beanvest.parser.ValueFormatException;

import java.math.BigDecimal;
import java.util.Optional;

public record Value(BigDecimal amount, String symbol, Optional<Value> originalValue) {
    public Value {
        if (originalValue.isPresent() && originalValue.get().originalValue.isPresent()) {
            throw new UnsupportedOperationException(
                    "while it might make sense at some point, currently throwing to point out a mistake");
        }
    }

    public Value(BigDecimal amount, String symbol) {
        this(amount, symbol, Optional.empty());
    }

    public static final Value ZERO = new Value(BigDecimal.ZERO, "");

    public Value(Value value, Optional<Value> value1) {
        this(value.amount, value.symbol, value1);
    }

    public Value(Value value, Value value1) {
        this(value, Optional.of(value1));
    }

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
        var actualSymbol = this.amount.compareTo(BigDecimal.ZERO) != 0 ? this.symbol : value.symbol;
        if (originalValue.isPresent() != value.originalValue.isPresent()) {
            throw new RuntimeException("only one side has original value");
        }
        return new Value(this.amount.add(value.amount), actualSymbol, originalValue.map(ov -> ov.add(value.originalValue.get())));
    }

    public Value add(BigDecimal value) {
        if (this.amount.compareTo(BigDecimal.ZERO) == 0) {
            throw new RuntimeException("currency needed");
        }
        return this.add(Value.of(value, symbol));
    }

    public Value subtract(Value value) {
        return this.add(value.negate());
    }

    public Value negate() {
        return new Value(this.getAmount().negate(), this.symbol, this.originalValue.map(Value::negate));
    }

    public String toString() {
        return this.amount.toString() + " " + this.symbol + originalValue.map(v -> " (" + v + ")").orElse("");
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
        if (!this.symbol.equals(value.getSymbol()) && !symbol.isEmpty() && !value.getSymbol().isEmpty()) {
            throw new ArithmeticException(String.format("cant operate on different commodities: %s and %s", this.symbol, value.getSymbol()));
        }
    }

    public Value withOriginalValue(Value value) {
        return new Value(this, value);
    }

    public Value multiply(BigDecimal units) {
        return new Value(amount.multiply(units), symbol, originalValue.map(v -> v.multiply(units)));
    }
}
