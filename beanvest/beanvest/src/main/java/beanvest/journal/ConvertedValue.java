package beanvest.journal;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class ConvertedValue extends Value {
    public static final int INTERNAL_SCALE = 10;
    private final Value convertedValue;

    public ConvertedValue(BigDecimal amount, String symbol, Value convertedValue) {
        super(amount, symbol);
        this.convertedValue = convertedValue;
    }

    public ConvertedValue(Value value, Value convertedValue) {
        this(value.amount(), value.symbol(), convertedValue);
    }

    public ConvertedValue(String value, String convertedValue) {
        this(Value.of(value), Value.of(convertedValue));
    }

    public Value convertedValue() {
        return convertedValue;
    }

    @Override
    public String toString() {
        return "ConvertedValue{" +
                super.toString() +
                ", originalValue=" + convertedValue +
                '}';
    }

    @Override
    public ConvertedValue add(Value value) {
        if (value instanceof ConvertedValue cv) {
            return new ConvertedValue(
                    asNonConvertedValue().add(cv.asNonConvertedValue()),
                    convertedValue.add(cv.convertedValue)
            );
        } else {
            return add(value.amount());
        }
    }

    @Override
    public ConvertedValue add(BigDecimal amount) {
        var proportion = amount.divide(amount(), INTERNAL_SCALE, RoundingMode.HALF_UP);
        var newConvertedAmount = convertedValue.amount().multiply(BigDecimal.ONE.add(proportion));

        return new ConvertedValue(super.add(amount), Value.of(newConvertedAmount, convertedValue().symbol()));
    }

    @Override
    public ConvertedValue negate() {
        return new ConvertedValue(super.negate(), convertedValue.negate());
    }

    @Override
    public ConvertedValue abs() {
        return new ConvertedValue(super.abs(), convertedValue.abs());
    }
}
