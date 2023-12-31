package beanvest.processor.deprecated.dto;

import beanvest.processor.time.Period;

import java.util.List;

public class PortfolioStatsDto {
    public final List<String> accounts;
    public final List<Period> periods;
    public final List<AccountDto> accountDtos;

    public PortfolioStatsDto(List<String> accounts, List<Period> periods, List<AccountDto> accountDtos) {
        this.accounts = accounts;
        this.periods = periods;
        this.accountDtos = accountDtos;
    }
}
