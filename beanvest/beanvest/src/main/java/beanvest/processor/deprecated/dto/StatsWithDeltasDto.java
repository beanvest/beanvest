package beanvest.processor.deprecated.dto;

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
                                 ValueStatDto xirrp,
                                 Collection<String> errors) {
}
