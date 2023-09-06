package beanvest.scripts.usagegen.generatesamplejournal;

import java.time.LocalDate;
import java.time.temporal.TemporalAmount;
import java.util.function.Consumer;

public record CoveredPeriod(LocalDate start, LocalDate end) {
    public boolean covers(LocalDate date) {
        return !date.isBefore(start) && !date.isAfter(end);
    }

    public void forEachDay(Consumer<LocalDate> dayConsumer) {
        var current = start;
        while (!current.isAfter(end)) {
            dayConsumer.accept(current);
            current = current.plusDays(1);
        }
    }

    public long days() {
        return end.toEpochDay() - start.toEpochDay();
    }
}