package beanvest.processor.dto;

import beanvest.result.Result;
import beanvest.result.StatErrors;

import java.math.BigDecimal;
import java.util.Map;

public record StatsV2(
        Map<String, Result<BigDecimal, StatErrors>> stats
) {
}
