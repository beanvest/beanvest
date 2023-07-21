package beanvest.tradingjournal;

import beanvest.tradingjournal.model.AccountState;
import beanvest.tradingjournal.model.entry.Entry;

import java.util.List;
import java.util.Map;

public interface JournalValidator {
    List<JournalValidationError> validate(List<Entry> dayops, Map<String, AccountState> accounts);
}
