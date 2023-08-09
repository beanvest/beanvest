package beanvest.processor.deprecated;

import beanvest.journal.entry.Entry;
import beanvest.processor.validation.ValidatorError;

import java.util.List;
import java.util.Map;

/**
 * @see StatsCollectingJournalProcessor
 * @deprecated processing model was rewritten, this is legacy and will be removed
 */
@Deprecated
public interface JournalValidator {
    List<ValidatorError> validate(List<Entry> dayops, Map<String, AccountState> accounts);
}
