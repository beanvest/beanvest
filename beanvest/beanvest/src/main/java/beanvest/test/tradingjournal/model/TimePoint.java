package beanvest.test.tradingjournal.model;

import java.time.LocalDate;
import java.util.Objects;

public final class TimePoint implements Comparable<TimePoint> {
    private final LocalDate lastDay;
    private final String title;

    public TimePoint(LocalDate lastDayDate, String title) {
        this.lastDay = lastDayDate;
        this.title = title;
    }

    @Override
    public int compareTo(TimePoint o) {
        return lastDay.compareTo(o.lastDay);
    }

    public boolean hasTitle() {
        return this.title() != null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (TimePoint) obj;
        return Objects.equals(this.lastDay, that.lastDay) &&
               Objects.equals(this.title, that.title);
    }

    @Override
    public String toString() {
        return "TimePoint[" +
               "date=" + lastDay + ", " +
               "title=" + title + ']';
    }

    public String title() {
        return title;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lastDay, title);
    }


}
