package beanvest.processor.processingv2.validator;

import beanvest.processor.processingv2.ProcessorV2;

import java.util.List;

public interface Validator extends ProcessorV2 {
    List<ValidatorError> getErrors();
}