package beanvest.processor.calendar;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Calendar {
    public List<Period> calculatePeriods(PeriodInterval interval, LocalDate start, LocalDate end) {
        if (interval == PeriodInterval.WHOLE) {
            return List.of(new Period(start, end, interval.getTitle(start)));
        }

        var actualStart = calculatePeriodStart(interval, start);

        return actuallyCalculatePeriods(interval, end, actualStart);
    }

    private ArrayList<Period> actuallyCalculatePeriods(PeriodInterval interval, LocalDate end, LocalDate actualStart) {
        var result = new ArrayList<Period>();
        var previous = actualStart;
        var current = increment(interval, actualStart);
        while (!current.isAfter(end)) {
            result.add(new Period(previous, current.minusDays(1), interval.getTitle(previous)));
            previous = current;
            current = increment(interval, current);
        }
        return result;
    }

    private static LocalDate calculatePeriodStart(PeriodInterval interval, LocalDate start) {
        var actualStart = switch (interval) {
            case MONTH -> LocalDate.of(start.getYear(), start.getMonth(), 1);
            case QUARTER -> LocalDate.of(start.getYear(), 1 + start.getMonthValue() / 3 * 3, 1);
            case YEAR -> LocalDate.of(start.getYear(), 1, 1);
            case WHOLE -> throw new UnsupportedOperationException("should not happen");
        };
        return actualStart;
    }

    private LocalDate increment(PeriodInterval interval, LocalDate date) {
        return switch (interval) {
            case MONTH -> date.plusMonths(1);
            case QUARTER -> date.plusMonths(3);
            case YEAR -> date.plusYears(1);
            case WHOLE -> throw new UnsupportedOperationException("should not happen");
        };
    }
}
