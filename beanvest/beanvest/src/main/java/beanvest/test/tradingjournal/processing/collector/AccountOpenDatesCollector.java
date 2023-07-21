package beanvest.test.tradingjournal.processing.collector;

import beanvest.test.tradingjournal.model.entry.Entry;
import beanvest.test.tradingjournal.Collector;
import beanvest.test.tradingjournal.model.entry.Close;

import java.time.LocalDate;
import java.util.Optional;

public class AccountOpenDatesCollector implements Collector {
    private LocalDate firstActivity;
    private LocalDate closingDate;

    @Override
    public void process(Entry entry) {
        if (firstActivity == null) {
            firstActivity = entry.date();
        }
        if (entry instanceof Close close) {
            closingDate = close.date();
        }
    }
    public Optional<LocalDate> getFirstActivity() {
        return Optional.ofNullable(firstActivity);
    }

    public Optional<LocalDate> getClosingDate() {
        return Optional.ofNullable(closingDate);
    }
}
