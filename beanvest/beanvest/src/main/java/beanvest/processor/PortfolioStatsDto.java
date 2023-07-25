package beanvest.processor;

import beanvest.processor.time.Period;

import java.util.List;

public class PortfolioStatsDto {
    public final List<String> accounts;
    public final List<Period> periods;
    public final List<AccountDto> stats;

    public PortfolioStatsDto(List<String> accounts, List<Period> periods, List<AccountDto> stats) {
        this.accounts = accounts;
        this.periods = periods;
        this.stats = stats;
    }
}
