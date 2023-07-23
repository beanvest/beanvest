package beanvest.processor;

public record StatsWithDeltasDto(StatDto deposits,
                                 StatDto withdrawals,
                                 StatDto interest,
                                 StatDto fees,
                                 StatDto dividends,
                                 StatDto realizedGain,
                                 StatDto cash,
                                 ValueStatDto unrealizedGains,
                                 ValueStatDto accountGain,
                                 ValueStatDto holdingsValue,
                                 ValueStatDto accountValue,
                                 ValueStatDto xirr) {
}
