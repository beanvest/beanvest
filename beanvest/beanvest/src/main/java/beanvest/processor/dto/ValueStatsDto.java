package beanvest.processor.dto;

import beanvest.result.Result;
import beanvest.result.UserError;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record ValueStatsDto(Result<BigDecimal, UserErrors> unrealizedGains,
                            Result<BigDecimal, UserErrors> accountGain,
                            Result<BigDecimal, UserErrors> holdingsValue,
                            Result<BigDecimal, UserErrors> accountValue,
                            Result<BigDecimal, UserErrors> xirr) {
    public Set<String> getErrorMessages() {
        return Stream.of(
                        unrealizedGains.getErrorOrNull(),
                        accountGain.getErrorOrNull(),
                        holdingsValue.getErrorOrNull(),
                        accountValue.getErrorOrNull(),
                        xirr.getErrorOrNull())
                .filter(Objects::nonNull)
                .flatMap(e -> e.errors.stream())
                .filter(UserError::hasMessage)
                .map(UserError::getMessage)
                .collect(Collectors.toSet());
    }
}
