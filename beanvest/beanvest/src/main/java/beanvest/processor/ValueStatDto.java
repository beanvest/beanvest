package beanvest.processor;

import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.util.Optional;

public record ValueStatDto(Result<BigDecimal, UserErrors> stat, Optional<BigDecimal> delta) {
}
