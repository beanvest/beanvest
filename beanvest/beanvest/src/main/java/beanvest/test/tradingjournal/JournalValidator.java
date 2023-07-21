package beanvest.test.tradingjournal;

import beanvest.test.tradingjournal.model.AccountState;
import beanvest.test.tradingjournal.model.entry.Entry;

import java.util.List;
import java.util.Map;

public interface JournalValidator {
    List<JournalValidationError> validate(List<Entry> dayops, Map<String, AccountState> accounts);
}
