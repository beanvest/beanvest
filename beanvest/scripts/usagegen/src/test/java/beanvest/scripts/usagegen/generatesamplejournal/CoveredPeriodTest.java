package beanvest.scripts.usagegen.generatesamplejournal;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CoveredPeriodTest {
    @Test
    void coversDate() {
        var coveredPeriod = new CoveredPeriod(LocalDate.parse("2022-01-03"), LocalDate.parse("2022-01-05"));
        assertThat(coveredPeriod.covers(LocalDate.parse("2022-01-02"))).isFalse();
        assertThat(coveredPeriod.covers(LocalDate.parse("2022-01-03"))).isTrue();
        assertThat(coveredPeriod.covers(LocalDate.parse("2022-01-04"))).isTrue();
        assertThat(coveredPeriod.covers(LocalDate.parse("2022-01-05"))).isTrue();
        assertThat(coveredPeriod.covers(LocalDate.parse("2022-01-06"))).isFalse();
    }
}