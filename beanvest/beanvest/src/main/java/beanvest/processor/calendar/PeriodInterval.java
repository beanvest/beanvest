package beanvest.processor.calendar;

import java.time.LocalDate;

public enum PeriodInterval {
    WHOLE,
    YEAR,
    QUARTER,
    MONTH;


    public String getTitle(LocalDate date) {
        return switch (this) {
            case WHOLE -> "TOTAL";
            case YEAR -> String.valueOf(date.getYear());
            case QUARTER -> String.format("%dq%d", date.getYear() % 100, (date.getMonthValue() - 1) / 3 + 1);
            case MONTH -> String.format("%dm%02d", date.getYear() % 100, date.getMonthValue());
        };
    }
}