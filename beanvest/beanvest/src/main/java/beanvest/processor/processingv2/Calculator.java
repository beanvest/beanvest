package beanvest.processor.processingv2;

import beanvest.result.Result;
import beanvest.result.StatErrors;

import java.math.BigDecimal;

public interface Calculator {
    Result<BigDecimal, StatErrors> calculate(CalculationParams params);
}
