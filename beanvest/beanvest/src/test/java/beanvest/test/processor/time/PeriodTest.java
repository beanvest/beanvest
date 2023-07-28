package beanvest.test.processor.time;

import beanvest.processor.processing.PeriodSpec;
import beanvest.processor.time.Period;
import beanvest.processor.time.PeriodInterval;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static beanvest.processor.time.PeriodInterval.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PeriodTest {

    @Test
    void noInterval() {
        var periodSpec = ps("2022-01-01", "2022-12-31", NONE);

        var period = Period.createPeriodCoveringDate(d("2020-06-02"), periodSpec);
        assertEquals(LocalDate.MIN, period.startDate());
        assertEquals(d("2021-12-31"), period.endDate());

        var next = period.next();
        assertEquals(d("2022-01-01"), next.startDate());
        assertEquals(d("2022-12-31"), next.endDate());
    }

    @Test
    void quarter() {
        var periodSpec = ps("2022-11-01", "2022-04-30", QUARTER);

        var period = Period.createPeriodCoveringDate(d("2021-12-10"), periodSpec);
        assertEquals(d("2021-10-01"), period.startDate());
        assertEquals(d("2021-12-31"), period.endDate());

        var next = period.next();
        assertEquals(d("2022-01-01"), next.startDate());
        assertEquals(d("2022-03-31"), next.endDate());

        var next2 = next.next();
        assertEquals(d("2022-04-01"), next2.startDate());
        assertEquals(d("2022-06-30"), next2.endDate());
    }

    @Test
    void month() {
        var periodSpec = ps("2021-11-10", "2022-01-14", MONTH);

        var period = Period.createPeriodCoveringDate(d("2021-11-05"), periodSpec);
        assertEquals(d("2021-11-01"), period.startDate());
        assertEquals(d("2021-11-30"), period.endDate());

        var next = period.next();
        assertEquals(d("2021-12-01"), next.startDate());
        assertEquals(d("2021-12-31"), next.endDate());

        var next2 = next.next();
        assertEquals(d("2022-01-01"), next2.startDate());
        assertEquals(d("2022-01-31"), next2.endDate());
    }

    private LocalDate d(String date) {
        return LocalDate.parse(date);
    }


    private static PeriodSpec ps(String requestedStart, String requestedEnd, PeriodInterval periodInterval) {
        return new PeriodSpec(LocalDate.parse(requestedStart), LocalDate.parse(requestedEnd), periodInterval);
    }
}