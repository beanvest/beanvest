package beanvest.processor.processing;

import beanvest.journal.entry.Entry;
import beanvest.processor.processing.calculator.StatCalculator;
import beanvest.result.ErrorFactory;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface Processor {
    void process(Entry entry);
}
