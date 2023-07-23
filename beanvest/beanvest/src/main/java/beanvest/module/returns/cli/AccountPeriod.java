package beanvest.module.returns.cli;

import beanvest.processor.StatsWithDeltasDto;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

public record AccountPeriod(
        String account,
        LocalDate openingDate,
        Optional<LocalDate> closingDate,
        Map<String, StatsWithDeltasDto> periodStats
) {
    public Optional<StatsWithDeltasDto> getStats(String period) {
        return Optional.ofNullable(periodStats.get(period));
    }
}
