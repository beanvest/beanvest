package beanvest.tradingjournal.processing;

import beanvest.tradingjournal.Period;
import beanvest.tradingjournal.model.entry.Entry;
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
        } else if (entry.date().isAfter(periods.get(currentPeriod).endDate())) {
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
        LOGGER.warn("FINISHED PERIOD: " + finishedPeriod);
        currentPeriod++;
        if (currentPeriod < periods.size()) {
            var currentPeriod = periods.get(this.currentPeriod);
            LOGGER.warn("NEW PERIOD: " + currentPeriod);
        }
        finishedPeriodConsumer.accept(finishedPeriod);
    }
}
