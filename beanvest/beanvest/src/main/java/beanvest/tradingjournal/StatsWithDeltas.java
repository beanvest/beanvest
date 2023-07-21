package beanvest.tradingjournal;

public record StatsWithDeltas(Stat deposits,
                              Stat withdrawals,
                              Stat interest,
                              Stat fees,
                              Stat dividends,
                              Stat realizedGain,
                              Stat cash,
                              ValueStat unrealizedGains,
                              ValueStat accountGain,
                              ValueStat holdingsValue,
                              ValueStat accountValue,
                              ValueStat xirr) {
}
