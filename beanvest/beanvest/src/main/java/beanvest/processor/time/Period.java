package beanvest.processor.time;

import beanvest.processor.processing.PeriodSpec;

import java.time.LocalDate;

public final class Period implements Comparable<Period> {
    private final LocalDate start;
    private final LocalDate end;
    private final PeriodSpec spec;

    private Period(LocalDate start, LocalDate end, PeriodSpec periodSpec) {
        this.start = start;
        this.end = end;
        spec = periodSpec;
    }

    public static Period createPeriodCoveringDate(LocalDate entryDate, PeriodSpec periodSpec) {
        var start = calculatePeriodStart(entryDate, periodSpec);
        var end = calculateActualEndDate(entryDate, start, periodSpec);
        var spec = periodSpec;
        return new Period(start, end, spec);
    }

    private static LocalDate calculateActualEndDate(LocalDate entryDate, LocalDate periodStart, PeriodSpec spec) {
        if (spec.interval() == PeriodInterval.NONE) { // ugh not sure if it fits here after all
            if (!periodStart.equals(LocalDate.MIN)) {
                return spec.end();
            }
            return entryDate.isBefore(spec.start()) ? spec.start().minusDays(1) : spec.end();
        }
        return calculateEndDate(spec.interval(), periodStart);
    }

    private static LocalDate calculatePeriodStart(LocalDate start, PeriodSpec periodSpec) {
        return switch (periodSpec.interval()) {
            case MONTH -> LocalDate.of(start.getYear(), start.getMonth(), 1);
            case QUARTER -> {
                var quarter = (((start.getMonthValue() - 1) / 3)) * 3 + 1;
                yield LocalDate.of(start.getYear(), quarter, 1);
            }
            case YEAR -> LocalDate.of(start.getYear(), 1, 1);
            case NONE -> !start.isAfter(periodSpec.start()) ? LocalDate.MIN : periodSpec.start();
        };
    }


    public Period next() {
        var start = this.end.plusDays(1);
        return new Period(start, calculateActualEndDate(start, start, spec), spec);
    }

    private static LocalDate calculateNextPeriodStartDate(PeriodInterval interval, LocalDate date) {
        return switch (interval) {
            case MONTH -> date.plusMonths(1);
            case QUARTER -> date.plusMonths(3);
            case YEAR -> date.plusYears(1);
            case NONE -> throw new UnsupportedOperationException("that doesnt make any sense");
        };
    }


    @Override
    public int compareTo(Period o) {
        return this.startDate().compareTo(o.startDate());
    }

    private static LocalDate calculateEndDate(PeriodInterval interval, LocalDate startDate) {
        return calculateNextPeriodStartDate(interval, startDate).minusDays(1);
    }

    public LocalDate startDate() {
        return start;
    }

    public PeriodInterval interval() {
        return spec.interval();
    }

    public LocalDate endDate() {
        return end;
    }

    public String title() {
        return getTitle(startDate());
    }

    private String getTitle(LocalDate date) {
        return switch (this.interval()) {
            case NONE -> "TOTAL";
            case YEAR -> String.valueOf(date.getYear());
            case QUARTER -> String.format("%dq%d", date.getYear() % 100, (date.getMonthValue() - 1) / 3 + 1);
            case MONTH -> String.format("%dm%02d", date.getYear() % 100, date.getMonthValue());
        };
    }
}
