package beanvest.processor.processingv2.dto;

import beanvest.processor.time.Period;

import java.util.List;

public record PortfolioStatsDto2(List<String> accounts, List<Period> periods, List<AccountDto2> accountDtos) {
}
