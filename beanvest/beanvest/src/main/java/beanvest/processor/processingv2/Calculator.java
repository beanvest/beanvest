package beanvest.processor.processingv2;

import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;

public interface Calculator {
    Result<BigDecimal, UserErrors> calculate(CalculationParams params);
}
