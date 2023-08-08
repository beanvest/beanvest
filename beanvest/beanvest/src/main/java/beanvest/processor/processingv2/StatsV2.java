package beanvest.processor.processingv2;

import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

public record StatsV2(
        Collection<String> errors,
        Map<String, Result<BigDecimal, UserErrors>> stats,
        AccountMetadata metadata
) {
}
