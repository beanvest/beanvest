package beanvest.test.processor.processing;

import beanvest.journal.Value;
import beanvest.journal.entry.Deposit;
import beanvest.journal.entry.Entry;
import beanvest.parser.SourceLine;
import beanvest.processor.processingv2.PeriodSpec;
import beanvest.journal.entity.Account2;
import beanvest.processor.time.Period;
import beanvest.processor.time.PeriodInterval;
import beanvest.processor.processingv2.EndOfPeriodTracker;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static beanvest.processor.processingv2.UnfinishedPeriodInclusion.EXCLUDE;
import static beanvest.processor.processingv2.UnfinishedPeriodInclusion.INCLUDE;
import static org.assertj.core.api.Assertions.assertThat;

class EndOfPeriodTrackerTest {

    @Test
    void shouldReportUnfinishedPeriods() {
        var finishedPeriods = new ArrayList<Period>();
        var spec = getSpec();
        var endOfPeriodTracker = new EndOfPeriodTracker(spec, INCLUDE, finishedPeriods::add);
        endOfPeriodTracker.process(createEntry("2022-01-01"));
        endOfPeriodTracker.finishPeriodsUpToEndDate();

        assertThat(finishedPeriods).hasSize(13);
    }

    @Test
    void shouldExcludeUnfinishedPeriods() {
        var finishedPeriods = new ArrayList<Period>();
        var spec = getSpec();
        var endOfPeriodTracker = new EndOfPeriodTracker(spec, EXCLUDE, finishedPeriods::add);
        endOfPeriodTracker.process(createEntry("2022-01-01"));
        endOfPeriodTracker.finishPeriodsUpToEndDate();

        assertThat(finishedPeriods).hasSize(12);
    }

    private static PeriodSpec getSpec() {
        return new PeriodSpec(LocalDate.MIN, LocalDate.parse("2023-01-10"), PeriodInterval.MONTH);
    }

    private Entry createEntry(String date) {
        return new Deposit(LocalDate.parse(date), Account2.fromStringId("anAccount"),
                Value.of("1", "GBP"), Optional.empty(), SourceLine.GENERATED_LINE);
    }
}