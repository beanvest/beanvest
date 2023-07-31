package beanvest.processor.processing.calculator;

import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;

public interface Calculator {
    Result<BigDecimal, UserErrors> calculate();
}
