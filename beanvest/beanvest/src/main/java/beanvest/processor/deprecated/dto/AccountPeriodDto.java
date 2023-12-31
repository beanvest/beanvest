package beanvest.processor.deprecated.dto;

import beanvest.processor.dto.StatsV2;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

public record AccountPeriodDto(
        String account,
        LocalDate openingDate,
        Optional<LocalDate> closingDate,
        Map<String, StatsV2> periodStats
) {
    public Optional<StatsV2> getStats(String period) {
        return Optional.ofNullable(periodStats.get(period));
    }
}
