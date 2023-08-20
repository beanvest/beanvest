package beanvest.processor.deprecated.dto;

import beanvest.result.Result;
import beanvest.result.StatErrors;

import java.math.BigDecimal;
import java.util.Optional;

public record ValueStatDto(Result<BigDecimal, StatErrors> stat, Optional<BigDecimal> delta) {
}
