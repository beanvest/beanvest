package beanvest.processor.processingv2.validator;

import beanvest.journal.entry.AccountOperation;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public interface Validator {
    List<ValidatorError> getErrors();

    void validate(AccountOperation op, Consumer<ValidatorError> errorConsumer);
}