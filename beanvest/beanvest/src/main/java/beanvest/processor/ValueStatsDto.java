package beanvest.processor;

import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;

public record ValueStatsDto(Result<BigDecimal, UserErrors> unrealizedGains,
                            Result<BigDecimal, UserErrors> accountGain,
                            Result<BigDecimal, UserErrors> holdingsValue,
                            Result<BigDecimal, UserErrors> accountValue,
                            Result<BigDecimal, UserErrors> xirr) {
}
