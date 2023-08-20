package beanvest.processor.processing;

import beanvest.journal.entry.Entry;
import beanvest.journal.entry.Price;
import beanvest.processor.processingv2.PeriodInclusion;
import beanvest.processor.processingv2.PeriodSpec;
import beanvest.processor.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.function.Consumer;

import static beanvest.processor.processingv2.PeriodInclusion.INCLUDE_UNFINISHED;
import static beanvest.processor.time.PeriodInterval.NONE;

public class EndOfPeriodTracker {
    private static final Logger LOGGER = LoggerFactory.getLogger(EndOfPeriodTracker.class.getName());
    private final PeriodSpec periodSpec;
    private final Consumer<Period> finishedPeriodConsumer;
    private final LocalDate end;
    private Period currentPeriod;


    public EndOfPeriodTracker(PeriodSpec periodSpec, PeriodInclusion periodInclusion, Consumer<Period> finishedPeriodConsumer) {
        this.periodSpec = periodSpec;
        this.finishedPeriodConsumer = finishedPeriodConsumer;
        if (periodInclusion == INCLUDE_UNFINISHED) {
            end = periodSpec.end();
        } else if (periodSpec.interval() == NONE) {
            end = periodSpec.start().equals(LocalDate.MIN) ? LocalDate.MIN : periodSpec.start().minusDays(1);
        } else {
            end = Period
                    .createPeriodCoveringDate(periodSpec.end(), periodSpec)
                    .startDate()
                    .minusDays(1);
        }
    }

    public void process(Entry entry) {
        if (this.currentPeriod == null && entry instanceof Price) {
            return;
        }
        if (this.currentPeriod == null) {
            currentPeriod = Period.createPeriodCoveringDate(entry.date(), periodSpec);
        }
        while (entry.date().isAfter(currentPeriod.endDate())) {
            finishCurrentPeriodAndStartNewOne();
        }
    }

    public void finishPeriodsUpToEndDate() {
        if (currentPeriod == null) {
            throw new RuntimeException("no transactions processed?");
        }
        if (currentPeriod.interval() == NONE) {
            var finishedOne = false;
            if (currentPeriod.startDate().equals(LocalDate.MIN)) {
                finishCurrentPeriodAndStartNewOne();
                finishedOne = true;
            }
            if (!finishedOne || !currentPeriod.endDate().equals(periodSpec.end())) {
                finishCurrentPeriod();
            }
        }
        if (currentPeriod.interval() != NONE) {
            while (!currentPeriod.startDate().isAfter(end)) {
                finishCurrentPeriodAndStartNewOne();
            }
        }
    }

    private void finishCurrentPeriodAndStartNewOne() {
        finishCurrentPeriod();
        currentPeriod = currentPeriod.next();
    }

    private void finishCurrentPeriod() {
        finishedPeriodConsumer.accept(currentPeriod);
    }
}
