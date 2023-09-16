package beanvest.journal;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ConvertedValueTest {
    @Test
    void addingValueAddsConvertedValueProportionally() {
        var gbp = new ConvertedValue(new BigDecimal(5), "GBP", Value.of("25 PLN"));
        var result = gbp.add(BigDecimal.ONE);

        assertThat(result.convertedValue()).isEqualTo(Value.of("30 PLN"));
    }

    @Test
    void subtractingValueSubtractsConvertedValueProportionally() {
        var gbp = new ConvertedValue(new BigDecimal(5), "GBP", Value.of("25 PLN"));
        var result = gbp.add(BigDecimal.ONE.negate());

        assertThat(result.convertedValue()).isEqualTo(Value.of("20 PLN"));
    }

    @Test
    void addingConvertedValueAddsConvertedValueExactly() {
        var gbp = new ConvertedValue("1 GBP", "5 PLN");
        var result = gbp.add(new ConvertedValue("1 GBP", "7 PLN"));

        assertThat(result.asNonConvertedValue()).isEqualTo(Value.of("2 GBP"));
        assertThat(result.convertedValue()).isEqualTo(Value.of("12 PLN"));
    }

    @Test
    void negateNegatesConvertedValueAsWell() {
        var gbp = new ConvertedValue(new BigDecimal(5), "GBP", Value.of("25 PLN"));
        var result = gbp.negate();

        assertThat(result.convertedValue()).isEqualTo(Value.of("-25 PLN"));
    }

    @Test
    void absTakesAbsOfConvertedValueAsWell() {
        var gbp = new ConvertedValue(new BigDecimal(-5), "GBP", Value.of("-25 PLN"));
        var result = gbp.abs();

        assertThat(result.convertedValue()).isEqualTo(Value.of("25 PLN"));
    }
}