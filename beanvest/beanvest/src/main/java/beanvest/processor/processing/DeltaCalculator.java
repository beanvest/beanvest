package beanvest.processor.processing;

import beanvest.processor.dto.StatDto;
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
                new StatDto(stats.deposits(), cashStatsDelta(stats, previous, Stats::deposits)),
                new StatDto(stats.withdrawals(), cashStatsDelta(stats, previous, Stats::withdrawals)),
                new StatDto(stats.interest(), cashStatsDelta(stats, previous, Stats::interest)),
                new StatDto(stats.fees(), cashStatsDelta(stats, previous, Stats::fees)),
                new StatDto(stats.dividends(), cashStatsDelta(stats, previous, Stats::dividends)),
                new StatDto(stats.realizedGains(), cashStatsDelta(stats, previous, Stats::realizedGains)),
                new StatDto(stats.cash(), cashStatsDelta(stats, previous, Stats::cash)),

                new ValueStatDto(stats.unrealizedGain(), calcValueStatsDelta(stats, previous, s -> s.unrealizedGain().asOptional())),
                new ValueStatDto(stats.getAccountGain(), calcValueStatsDelta(stats, previous, s -> s.getAccountGain().asOptional())),
                new ValueStatDto(stats.holdingsValue(), calcValueStatsDelta(stats, previous, s -> s.holdingsValue().asOptional())),
                new ValueStatDto(stats.accountValue(), calcValueStatsDelta(stats, previous, s -> s.accountValue().asOptional())),
                new ValueStatDto(stats.xirrValue(), calcValueStatsDelta(stats, previous, s -> s.xirrValue().asOptional())),
        stats.errors());
        previousStats.put(account, stats);
        return statsWithDeltas;
    }

    private static Optional<BigDecimal> cashStatsDelta(Stats current, Optional<Stats> prev, Function<Stats, BigDecimal> extractor) {
        var value = extractor.apply(current);
        if (prev.isEmpty()) {
            return Optional.of(value);
        }
        var previousValue = extractor.apply(prev.get());
        return Optional.of(value.subtract(previousValue));
    }

    private static Optional<BigDecimal> calcValueStatsDelta(Stats current, Optional<Stats> maybePrevStats, Function<Stats, Optional<BigDecimal>> extractor) {
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
