package beanvest.tradingjournal.processing;

import beanvest.tradingjournal.Stat;
import beanvest.tradingjournal.Stats;
import beanvest.tradingjournal.StatsWithDeltas;
import beanvest.tradingjournal.ValueStat;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class DeltaCalculator {
    private final Map<String, Stats> previousStats = new HashMap<>();

    public StatsWithDeltas calculateDeltas(String account, Stats stats) {
        Optional<Stats> previous = Optional.ofNullable(previousStats.get(account));

        var statsWithDeltas = new StatsWithDeltas(
                new Stat(stats.deposits(), cashStatsDelta(stats, previous, Stats::deposits)),
                new Stat(stats.withdrawals(), cashStatsDelta(stats, previous, Stats::withdrawals)),
                new Stat(stats.interest(), cashStatsDelta(stats, previous, Stats::interest)),
                new Stat(stats.fees(), cashStatsDelta(stats, previous, Stats::fees)),
                new Stat(stats.dividends(), cashStatsDelta(stats, previous, Stats::dividends)),
                new Stat(stats.realizedGains(), cashStatsDelta(stats, previous, Stats::realizedGains)),
                new Stat(stats.cash(), cashStatsDelta(stats, previous, Stats::cash)),

                new ValueStat(stats.unrealizedGain(), calcValueStatsDelta(stats, previous, s -> s.unrealizedGain().asOptional())),
                new ValueStat(stats.getAccountGain(), calcValueStatsDelta(stats, previous, s -> s.getAccountGain().asOptional())),
                new ValueStat(stats.holdingsValue(), calcValueStatsDelta(stats, previous, s -> s.holdingsValue().asOptional())),
                new ValueStat(stats.accountValue(), calcValueStatsDelta(stats, previous, s -> s.accountValue().asOptional())),
                new ValueStat(stats.xirrValue(), calcValueStatsDelta(stats, previous, s -> s.xirrValue().asOptional()))
        );
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
