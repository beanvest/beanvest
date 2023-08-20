package beanvest.processor.deprecated.dto;

import beanvest.result.Result;
import beanvest.result.StatError;
import beanvest.result.StatErrors;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record ValueStatsDto(Result<BigDecimal, StatErrors> unrealizedGains,
                            Result<BigDecimal, StatErrors> accountGain,
                            Result<BigDecimal, StatErrors> holdingsValue,
                            Result<BigDecimal, StatErrors> accountValue,
                            Result<BigDecimal, StatErrors> xirr,
                            Result<BigDecimal, StatErrors> xirrp) {
    public Set<String> getErrorMessages() {
        return Stream.of(
                        unrealizedGains.getErrorOrNull(),
                        accountGain.getErrorOrNull(),
                        holdingsValue.getErrorOrNull(),
                        accountValue.getErrorOrNull(),
                        xirr.getErrorOrNull(),
                        xirrp.getErrorOrNull()
                )
                .filter(Objects::nonNull)
                .flatMap(e -> e.errors.stream())
                .filter(StatError::hasMessage)
                .map(StatError::getMessage)
                .collect(Collectors.toSet());
    }
}
