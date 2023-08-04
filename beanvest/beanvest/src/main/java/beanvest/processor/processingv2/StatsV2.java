package beanvest.processor.processingv2;

import beanvest.processor.processing.AccountMetadata;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

public class StatsV2 {
    private final Collection<String> errors;
    private final Map<Class<?>, Result<BigDecimal, UserErrors>> stats;

    public StatsV2(Collection<String> errors, Map<Class<?>, Result<BigDecimal, UserErrors>> stats, AccountMetadata metadata) {
        this.errors = errors;

        this.stats = stats;
    }
}
