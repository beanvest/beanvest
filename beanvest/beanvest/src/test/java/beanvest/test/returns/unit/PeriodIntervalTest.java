package beanvest.test.returns.unit;

import beanvest.test.tradingjournal.Calendar;
import beanvest.test.tradingjournal.PeriodInterval;
import beanvest.test.tradingjournal.Period;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PeriodIntervalTest {
    @Test
    void yearly() {
        var periods = new Calendar().getPeriods(PeriodInterval.YEAR,
                LocalDate.of(2022, 2, 1),
                LocalDate.of(2024, 3, 1)
        );
        assertThat(periods).isEqualTo(List.of(
                new Period(LocalDate.parse("2022-01-01"), LocalDate.parse("2022-12-31"), "2022"),
                new Period(LocalDate.parse("2023-01-01"), LocalDate.parse("2023-12-31"), "2023")
        ));
    }

    @Test
    void quarterly() {
        var periods = new Calendar().getPeriods(PeriodInterval.QUARTER,
                LocalDate.of(2022, 11, 5),
                LocalDate.of(2023, 5, 1)
        );
        assertThat(periods).isEqualTo(List.of(
                new Period(LocalDate.parse("2022-10-01"), LocalDate.parse("2022-12-31"), "22q4"),
                new Period(LocalDate.parse("2023-01-01"), LocalDate.parse("2023-03-31"), "23q1")
        ));
    }
    @Test
    void monthly() {
        var periods = new Calendar().getPeriods(PeriodInterval.MONTH,
                LocalDate.of(2022, 12, 1),
                LocalDate.of(2023, 2, 1)
        );
        assertThat(periods).isEqualTo(List.of(
                new Period(LocalDate.parse("2022-12-01"), LocalDate.parse("2022-12-31"), "22m12"),
                new Period(LocalDate.parse("2023-01-01"), LocalDate.parse("2023-01-31"), "23m01")
        ));
    }
}