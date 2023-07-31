package beanvest.processor.processing;

import beanvest.journal.Stats;
import beanvest.processor.dto.StatsWithDeltasDto;
import beanvest.processor.dto.ValueStatDto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class DeltaCalculator {
    private final Map<String, Stats> previousStats = new HashMap<>();

    public StatsWithDeltasDto calculateDeltas(String account, Stats stats) {
        Optional<Stats> previous = Optional.ofNullable(previousStats.get(account));

        var statsWithDeltas = new StatsWithDeltasDto(
                new ValueStatDto(stats.deposits(), calculateStatsDelta(stats, previous, stats2 -> stats2.deposits().asOptional())),
                new ValueStatDto(stats.withdrawals(), calculateStatsDelta(stats, previous, stats3 -> stats3.withdrawals().asOptional())),
                new ValueStatDto(stats.interest(), calculateStatsDelta(stats, previous, stats4 -> stats4.interest().asOptional())),
                new ValueStatDto(stats.fees(), calculateStatsDelta(stats, previous, stats5 -> stats5.fees().asOptional())),
                new ValueStatDto(stats.dividends(), calculateStatsDelta(stats, previous, stats6 -> stats6.dividends().asOptional())),
                new ValueStatDto(stats.realizedGains(), calculateStatsDelta(stats, previous, stats7 -> stats7.realizedGains().asOptional())),
                new ValueStatDto(stats.cash(), calculateStatsDelta(stats, previous, stats1 -> stats1.cash().asOptional())),

                new ValueStatDto(stats.unrealizedGain(), calculateStatsDelta(stats, previous, s -> s.unrealizedGain().asOptional())),
                new ValueStatDto(stats.getAccountGain(), calculateStatsDelta(stats, previous, s -> s.getAccountGain().asOptional())),
                new ValueStatDto(stats.holdingsValue(), calculateStatsDelta(stats, previous, s -> s.holdingsValue().asOptional())),
                new ValueStatDto(stats.accountValue(), calculateStatsDelta(stats, previous, s -> s.accountValue().asOptional())),
                new ValueStatDto(stats.xirrValue(), calculateStatsDelta(stats, previous, s -> s.xirrValue().asOptional())),
        stats.errors());
        previousStats.put(account, stats);
        return statsWithDeltas;
    }

    private static Optional<BigDecimal> calculateStatsDelta(Stats current, Optional<Stats> maybePrevStats, Function<Stats, Optional<BigDecimal>> extractor) {
        var maybeCurrentValue = extractor.apply(current);

        if (maybeCurrentValue.isEmpty()) {
            return Optional.empty();
        }
        Optional<BigDecimal> prevValue;
        if (maybePrevStats.isEmpty()) {
            prevValue = Optional.of(BigDecimal.ZERO);
        } else {
            prevValue = extractor.apply(maybePrevStats.get());
        }
        var value = maybeCurrentValue.get();

        return prevValue.map(value::subtract);
    }
}
