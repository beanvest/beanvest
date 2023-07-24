package beanvest.processor.processing;

import beanvest.processor.calendar.Period;
import beanvest.journal.entry.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Consumer;

public class EndOfPeriodTracker {
    private static final Logger LOGGER = LoggerFactory.getLogger(EndOfPeriodTracker.class.getName());
    private final Consumer<Period> finishedPeriodConsumer;
    private final List<Period> periods;
    private Integer currentPeriod;


    public EndOfPeriodTracker(List<Period> periods, Consumer<Period> finishedPeriodConsumer) {
        this.periods = periods;
        this.finishedPeriodConsumer = finishedPeriodConsumer;
    }

    public void process(Entry entry) {
        if (this.currentPeriod == null) {
            currentPeriod = 0;
        }
        while (entry.date().isAfter(periods.get(currentPeriod).endDate())) {
            finishCurrentPeriod();
        }
    }

    public void finishRemainingPeriods() {
        while (currentPeriod < periods.size()) {
            finishCurrentPeriod();
        }
    }

    private void finishCurrentPeriod() {
        var finishedPeriod = periods.get(currentPeriod);
        currentPeriod++;
        finishedPeriodConsumer.accept(finishedPeriod);
    }
}
