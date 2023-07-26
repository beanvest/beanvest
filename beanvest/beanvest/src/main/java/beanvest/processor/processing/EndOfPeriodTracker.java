package beanvest.processor.processing;

import beanvest.journal.entry.Price;
import beanvest.journal.entry.Entry;
import beanvest.processor.time.Period;
import beanvest.processor.time.PeriodInterval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.function.Consumer;

public class EndOfPeriodTracker {
    private static final Logger LOGGER = LoggerFactory.getLogger(EndOfPeriodTracker.class.getName());
    private final LocalDate endDate;
    private final PeriodSpec periodSpec;
    private final Consumer<Period> finishedPeriodConsumer;
    private Period currentPeriod;


    public EndOfPeriodTracker(PeriodSpec periodSpec, Consumer<Period> finishedPeriodConsumer) {
        this.endDate = Period.calculateActualEndDate(periodSpec.interval(), periodSpec.periodsInclusion(), periodSpec.end());
        this.periodSpec = periodSpec;
        this.finishedPeriodConsumer = finishedPeriodConsumer;
    }

    public void process(Entry entry) {
        if (this.currentPeriod == null && entry instanceof Price) {
            return;
        }
        if (this.currentPeriod == null) {
            currentPeriod = Period.createPeriodCoveringDate(entry.date(), endDate, periodSpec.interval());
        }
        while (entry.date().isAfter(currentPeriod.endDate())) {
            finishCurrentPeriod();
        }
    }

    public void finishPeriodsUpToEndDate() {
        if (currentPeriod.interval() == PeriodInterval.NONE) {
            finishCurrentPeriod();
        } else {
            while (!currentPeriod.startDate().isAfter(endDate)) {
                finishCurrentPeriod();
            }
        }
    }

    private void finishCurrentPeriod() {
        finishedPeriodConsumer.accept(currentPeriod);
        if (currentPeriod.interval() != PeriodInterval.NONE) {
            currentPeriod = currentPeriod.next();
        }
    }

}
