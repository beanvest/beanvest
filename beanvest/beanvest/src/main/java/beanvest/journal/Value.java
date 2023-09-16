package beanvest.journal;

import beanvest.parser.ValueFormatException;

import java.math.BigDecimal;
import java.util.Objects;

public sealed class Value permits ConvertedValue {
    public static final Value ZERO = new Value(BigDecimal.ZERO, "");
    private final BigDecimal amount;
    private final String symbol;

    public Value(BigDecimal amount, String symbol) {
        this.amount = amount;
        this.symbol = symbol;
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
        if (!value.getClass().equals(this.getClass())) {
            throw new RuntimeException("trying to add converted value to a non-converted one");
        }
        verifySameSymbol(value);
        return new Value(value.getAmount().add(this.amount), this.amount.equals(BigDecimal.ZERO) ? value.getSymbol() : this.symbol);
    }

    public Value add(BigDecimal value) {
        return new Value(this.amount.add(value), this.symbol);
    }

    public Value negate() {
        return new Value(this.getAmount().negate(), this.symbol);
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

    public BigDecimal amount() {
        return amount;
    }

    public String symbol() {
        return symbol;
    }

    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        return amount.compareTo(((Value) obj).amount) == 0
                && symbol.equals(((Value) obj).symbol);
    }

    public int hashCode() {
        return Objects.hash(amount, symbol);
    }

    public String toString() {
        return "Value{" + "amount=" + amount + ", symbol='" + symbol + '\'' + '}';
    }

    public String toPlainString()
    {
        return this.amount.toString() + " " + this.symbol;
    }


    public Value asNonConvertedValue() {
        return Value.of(amount, symbol);
    }
}
