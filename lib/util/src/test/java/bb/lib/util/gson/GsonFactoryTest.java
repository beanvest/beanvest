package bb.lib.util.gson;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;

class GsonFactoryTest {

    public static final double DEFAULT_PRECISION = 0.00001;

    @Test
    void testConvertingDouble() {
        var gson = GsonFactory.createWithProjectDefaults();
        var input = 2.4f;
        var s = gson.fromJson(gson.toJsonTree(input), Double.class);
        assertThat(s).isEqualTo(input, withPrecision(DEFAULT_PRECISION));
    }

    @Test
    void testConvertingOptionalDouble() {
        var gson = GsonFactory.createWithProjectDefaults();
        var input = 0.014d;
        var json = gson.toJsonTree(new OptionalDouble(input));
        var s = gson.fromJson(json, OptionalDouble.class);
        assertThat(s.a.get()).isEqualTo(input, withPrecision(DEFAULT_PRECISION));
    }

    @Test
    void testConvertingLocalDate() {
        var gson = GsonFactory.createWithProjectDefaults();
        var input = LocalDate.parse("2014-01-03");
        var json = gson.toJsonTree(input);
        var s = gson.fromJson(json, LocalDate.class);
        assertThat(s).isEqualTo(input);
    }

    static class OptionalDouble {
        public Optional<Double> a;

        public OptionalDouble(Double a) {
            this.a = Optional.ofNullable(a);
        }
    }
}