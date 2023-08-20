package beanvest.processor.processingv2.dto;

import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.util.Map;

public record StatsV2(
        Map<String, Result<BigDecimal, UserErrors>> stats
) {
}
