package beanvest.processor.processingv2;

import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Close;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AccountOpenDatesCollector implements Processor {
    private Map<String, LocalDate> firstActivity = new HashMap<>();
    private Map<String, LocalDate> closingDate = new HashMap<>();

    @Override
    public void process(AccountOperation op) {
        if (firstActivity == null) {
            firstActivity.put(op.account(), op.date());
        }
        if (op instanceof Close close) {
            closingDate.put(op.account(), close.date());
        }
    }
    public Optional<LocalDate> getFirstActivity(String account) {
        return Optional.ofNullable(firstActivity.get(account));
    }

    public Optional<LocalDate> getClosingDate(String account) {
        return Optional.ofNullable(closingDate.get(account));
    }
}
