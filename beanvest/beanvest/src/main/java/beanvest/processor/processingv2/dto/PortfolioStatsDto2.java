package beanvest.processor.processingv2.dto;

import java.util.List;
import java.util.Set;

public record PortfolioStatsDto2(
        List<String> accounts,
        List<String> periods,
        List<String> stats,
        List<AccountDto2> accountDtos
) {
}
