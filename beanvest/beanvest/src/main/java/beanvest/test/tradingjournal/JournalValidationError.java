package beanvest.test.tradingjournal;

import beanvest.test.tradingjournal.model.UserError;
import beanvest.test.tradingjournal.model.UserErrorId;

import java.util.List;

public class JournalValidationError extends UserError {
    public final String journalLine;
    public final List<String> details;

    public JournalValidationError(String message, String journalLine, List<String> details) {
        super(UserErrorId.VALIDATION_ERROR, message);
        this.journalLine = journalLine;
        this.details = details;
    }
}
