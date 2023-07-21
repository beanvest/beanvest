package beanvest.tradingjournal;

import beanvest.tradingjournal.model.UserErrors;

import java.math.BigDecimal;
import java.util.Optional;

public record ValueStat(Result<BigDecimal, UserErrors> stat, Optional<BigDecimal> delta) {
}