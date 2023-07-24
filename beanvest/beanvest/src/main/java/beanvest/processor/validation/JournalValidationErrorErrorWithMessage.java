package beanvest.processor.validation;

import beanvest.result.ErrorEnum;
import beanvest.result.UserError;

public class JournalValidationErrorErrorWithMessage extends UserError {
    public JournalValidationErrorErrorWithMessage(String message, String journalLine) {
        super(ErrorEnum.VALIDATION_ERROR, message + "\n  @ " + journalLine);
    }
}
