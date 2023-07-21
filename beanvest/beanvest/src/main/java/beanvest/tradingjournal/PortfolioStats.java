package beanvest.tradingjournal;

import java.util.List;

public class PortfolioStats {
    public final List<String> accounts;
    public final CollectionMode collectionMode;
    public final List<Period> periods;
    public final List<AccountDto> stats;

    public PortfolioStats(List<String> accounts, CollectionMode collectionMode, List<Period> periods, List<AccountDto> stats) {
        this.accounts = accounts;
        this.collectionMode = collectionMode;
        this.periods = periods;
        this.stats = stats;
    }
}
