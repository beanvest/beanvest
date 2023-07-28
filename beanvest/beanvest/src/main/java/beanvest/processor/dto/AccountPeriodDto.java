package beanvest.processor.dto;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

public record AccountPeriodDto(
        String account,
        LocalDate openingDate,
        Optional<LocalDate> closingDate,
        Map<String, StatsWithDeltasDto> periodStats
) {
    public Optional<StatsWithDeltasDto> getStats(String period) {
        return Optional.ofNullable(periodStats.get(period));
    }
}
