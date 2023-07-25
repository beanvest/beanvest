package beanvest.processor.time;

import beanvest.processor.processing.EndOfPeriodTracker.PeriodInclusion;

import java.time.LocalDate;
import java.util.Objects;

public final class Period implements Comparable<Period> {
    private final LocalDate start;
    private final LocalDate end;
    private final PeriodInterval interval;

    private Period(LocalDate start, LocalDate end, PeriodInterval interval) {
        this.start = start;
        this.interval = interval;
        this.end = interval == PeriodInterval.WHOLE ? end : calculateEndDate(interval, start);
    }

    public static Period createPeriodCoveringDate(LocalDate parse, LocalDate end, PeriodInterval periodInterval) {
        return new Period(calculatePeriodStart(periodInterval, parse), end, periodInterval);
    }

    public static LocalDate calculateActualEndDate(PeriodInterval interval, PeriodInclusion periodInclusion, LocalDate endDate) {
        var start1 = Period.createPeriodCoveringDate(endDate, endDate, interval);
        if (start1.interval == PeriodInterval.WHOLE) { // ugh not sure if it fits here after all
            return endDate;
        }
        if (periodInclusion == PeriodInclusion.INCLUDE_UNFINISHED) {
            return start1.endDate();
        } else {
            return start1.startDate().minusDays(1);
        }
    }

    public Period next() {
        return new Period(calculateNextPeriodStartDate(interval, start), end, interval);
    }

    private LocalDate calculateNextPeriodStartDate(PeriodInterval interval, LocalDate date) {
        return switch (interval) {
            case MONTH -> date.plusMonths(1);
            case QUARTER -> date.plusMonths(3);
            case YEAR -> date.plusYears(1);
            case WHOLE -> throw new UnsupportedOperationException("that doesnt make any sense");
        };
    }

    private static LocalDate calculatePeriodStart(PeriodInterval interval, LocalDate start) {
        return switch (interval) {
            case MONTH -> LocalDate.of(start.getYear(), start.getMonth(), 1);
            case QUARTER -> LocalDate.of(start.getYear(), 1 + start.getMonthValue() / 3 * 3, 1);
            case YEAR -> LocalDate.of(start.getYear(), 1, 1);
            case WHOLE -> LocalDate.MIN;
        };
    }


    @Override
    public int compareTo(Period o) {
        return this.start.compareTo(o.start);
    }

    private LocalDate calculateEndDate(PeriodInterval interval, LocalDate startDate) {
        return calculateNextPeriodStartDate(interval, startDate).minusDays(1);
    }

    public LocalDate startDate() {
        return start;
    }

    public PeriodInterval interval() {
        return interval;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Period) obj;
        return Objects.equals(this.start, that.start) &&
               Objects.equals(this.interval, that.interval);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, interval);
    }

    @Override
    public String toString() {
        return "Period2[" +
               "startDate=" + start + ", " +
               "interval=" + interval + ']';
    }

    public LocalDate endDate() {
        return end;
    }

    public String title() {
        return interval.getTitle(startDate());
    }
}
