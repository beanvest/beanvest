package beanvest.processor.calendar;

import java.time.LocalDate;

public record Period(LocalDate startDate, LocalDate endDate, String title) implements Comparable<Period> {
    @Override
    public int compareTo(Period o) {
        return this.startDate.compareTo(o.startDate);
    }
}
