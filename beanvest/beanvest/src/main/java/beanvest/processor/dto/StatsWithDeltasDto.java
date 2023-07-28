package beanvest.processor.dto;

import java.util.Collection;

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
                                 ValueStatDto xirr,
                                 Collection<String> errors) {
}
