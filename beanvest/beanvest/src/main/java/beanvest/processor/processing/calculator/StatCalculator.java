package beanvest.processor.processing.calculator;

import beanvest.journal.entry.Entry;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface StatCalculator {
    void process(Entry entry);

    Result<BigDecimal, UserErrors> calculate(final LocalDate endDate, String targetCurrency);
}
