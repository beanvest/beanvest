package beanvest.processor.processingv2;

import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface Calculator {
    Result<BigDecimal, UserErrors> calculate(final Entity entity, final LocalDate endDate, String targetCurrency);
}
