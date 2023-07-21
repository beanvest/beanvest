package beanvest.returns.cli;

import beanvest.tradingjournal.StatsWithDeltas;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

public record AccountPeriod(
        String account,
        LocalDate openingDate,
        Optional<LocalDate> closingDate,
        Map<String, StatsWithDeltas> periodStats
) {
    public Optional<StatsWithDeltas> getStats(String period) {
        return Optional.ofNullable(periodStats.get(period));
    }
}
