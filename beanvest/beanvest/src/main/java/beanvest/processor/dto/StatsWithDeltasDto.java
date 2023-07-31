package beanvest.processor.dto;

import java.util.Collection;

public record StatsWithDeltasDto(ValueStatDto deposits,
                                 ValueStatDto withdrawals,
                                 ValueStatDto interest,
                                 ValueStatDto fees,
                                 ValueStatDto dividends,
                                 ValueStatDto realizedGain,
                                 ValueStatDto cash,
                                 ValueStatDto unrealizedGains,
                                 ValueStatDto accountGain,
                                 ValueStatDto holdingsValue,
                                 ValueStatDto accountValue,
                                 ValueStatDto xirr,
                                 Collection<String> errors) {
}
