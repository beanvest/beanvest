package beanvest.test.tradingjournal;

import java.time.LocalDate;

public enum PeriodInterval {
    YEAR,
    QUARTER,
    MONTH;


    public String getTitle(LocalDate date) {
        return switch (this) {
            case YEAR -> String.valueOf(date.getYear());
            case QUARTER -> String.format("%dq%d", date.getYear() % 100, (date.getMonthValue() - 1) / 3 + 1);
            case MONTH -> String.format("%dm%02d", date.getYear() % 100, date.getMonthValue());
        };
    }
}