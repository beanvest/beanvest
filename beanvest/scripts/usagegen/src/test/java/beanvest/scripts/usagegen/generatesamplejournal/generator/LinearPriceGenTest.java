package beanvest.scripts.usagegen.generatesamplejournal.generator;

import beanvest.scripts.usagegen.generatesamplejournal.CoveredPeriod;
import beanvest.scripts.usagegen.generatesamplejournal.generator.JournalWriter.JournalEntry;
import beanvest.scripts.usagegen.generatesamplejournal.generator.price.LinearPriceGen;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LinearPriceGenTest {

    @Test
    void shouldMovePriceLinearlyAndWriteUpdatesAtTheEndOfEachMonth() {
        var period = new CoveredPeriod(LocalDate.parse("2023-01-01"), LocalDate.parse("2023-02-28"));
        JournalWriter writer = JournalWriter.createPriceWriter("prices_spx");
        LinearPriceGen gen = new LinearPriceGen("SPX", period, "100", "200", writer);

        period.forEachDay(gen::generate);

        var entries = writer.getEntries();
        assertThat(entries).isEqualTo(List.of(
                new JournalEntry(LocalDate.parse("2023-01-28"), "price SPX 146.55 GBP"),
                new JournalEntry(LocalDate.parse("2023-02-28"), "price SPX 200.00 GBP")
        ));
    }

    @Test
    void shouldMovePriceLinearlyAndWriteUpdatesAtTheEndOfEachMonthEvenEndingOnSomeOtherDay() {
        var period = new CoveredPeriod(LocalDate.parse("2023-01-01"), LocalDate.parse("2023-04-15"));
        JournalWriter writer = JournalWriter.createPriceWriter("prices_spx");
        LinearPriceGen gen = new LinearPriceGen("SPX", period, "100", "150", writer);

        period.forEachDay(gen::generate);

        var entries = writer.getEntries();
        assertThat(entries).isEqualTo(List.of(
                new JournalEntry(LocalDate.parse("2023-01-28"), "price SPX 112.98 GBP"),
                new JournalEntry(LocalDate.parse("2023-02-28"), "price SPX 127.88 GBP"),
                new JournalEntry(LocalDate.parse("2023-03-28"), "price SPX 141.34 GBP")
        ));
    }
}