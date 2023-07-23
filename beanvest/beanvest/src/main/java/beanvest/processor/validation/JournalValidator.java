package beanvest.processor.validation;

import beanvest.processor.deprecated.AccountState;
import beanvest.journal.entry.Entry;

import java.util.List;
import java.util.Map;

public interface JournalValidator {
    List<JournalValidationError> validate(List<Entry> dayops, Map<String, AccountState> accounts);
}
