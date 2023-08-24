package beanvest.processor.dto;

import java.util.List;
import java.util.Map;

public record PortfolioStatsDto2(
        Map<String, AccountDetailsDto> accounts,
        List<String> periods,
        List<String> stats,
        List<AccountDto2> accountDtos,
        List<String> userErrors) {
}
