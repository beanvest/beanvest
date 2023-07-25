package beanvest.test.processor.time;

import beanvest.processor.time.Period;
import beanvest.processor.time.PeriodInterval;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class Period2Test {
    @Test
    void coveringDateYear() {
        var p = Period.createPeriodCoveringDate(LocalDate.parse("2021-05-01"), LocalDate.MAX, PeriodInterval.YEAR);
        assertThat(p.startDate())
                .isEqualTo(LocalDate.parse("2021-01-01"));
    }

    @Test
    void coveringDateQuarter() {
        var p = Period.createPeriodCoveringDate(LocalDate.parse("2021-05-01"), LocalDate.MAX, PeriodInterval.QUARTER);
        assertThat(p.startDate())
                .isEqualTo(LocalDate.parse("2021-04-01"));
    }

    @Test
    void coveringDateMonth() {
        var p = Period.createPeriodCoveringDate(LocalDate.parse("2021-05-01"), LocalDate.MAX, PeriodInterval.MONTH);
        assertThat(p.startDate())
                .isEqualTo(LocalDate.parse("2021-05-01"));
    }

    @Test
    void nextMonth() {
        var p = Period.createPeriodCoveringDate(LocalDate.parse("2021-05-10"), LocalDate.MAX, PeriodInterval.MONTH);
        assertThat(p.next().startDate())
                .isEqualTo(LocalDate.parse("2021-06-01"));
        assertThat(p.next().next().startDate())
                .isEqualTo(LocalDate.parse("2021-07-01"));
    }

    @Test
    void nextQuarter() {
        var p = Period.createPeriodCoveringDate(LocalDate.parse("2021-05-10"), LocalDate.MAX, PeriodInterval.QUARTER);
        assertThat(p.next().startDate())
                .isEqualTo(LocalDate.parse("2021-07-01"));
        assertThat(p.next().next().startDate())
                .isEqualTo(LocalDate.parse("2021-10-01"));
    }


    @Test
    void nextYear() {
        var p = Period.createPeriodCoveringDate(LocalDate.parse("2021-05-10"), LocalDate.MAX, PeriodInterval.YEAR);
        assertThat(p.next().startDate())
                .isEqualTo(LocalDate.parse("2022-01-01"));
        assertThat(p.next().next().startDate())
                .isEqualTo(LocalDate.parse("2023-01-01"));
    }

    @Test
    void endMonth() {
        var p = Period.createPeriodCoveringDate(LocalDate.parse("2021-05-10"), LocalDate.MAX, PeriodInterval.MONTH);
        assertThat(p.endDate())
                .isEqualTo(LocalDate.parse("2021-05-31"));
        assertThat(p.next().endDate())
                .isEqualTo(LocalDate.parse("2021-06-30"));
    }

    @Test
    void endQuarter() {
        var p = Period.createPeriodCoveringDate(LocalDate.parse("2021-05-10"), LocalDate.MAX, PeriodInterval.QUARTER);
        assertThat(p.endDate())
                .isEqualTo(LocalDate.parse("2021-06-30"));
        assertThat(p.next().endDate())
                .isEqualTo(LocalDate.parse("2021-09-30"));
    }

    @Test
    void endYear() {
        var p = Period.createPeriodCoveringDate(LocalDate.parse("2021-05-10"), LocalDate.MAX, PeriodInterval.YEAR);
        assertThat(p.endDate())
                .isEqualTo(LocalDate.parse("2021-12-31"));
        assertThat(p.next().endDate())
                .isEqualTo(LocalDate.parse("2022-12-31"));
    }
}