package beanvest.processor.deprecated;

import beanvest.processor.processingv2.validator.ValidatorError;
import beanvest.journal.CashFlow;
import beanvest.journal.Holdings;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @see StatsCollectingJournalProcessor
 * @deprecated processing model was rewritten, this is legacy and will be removed
 */
@Deprecated
public record JournalState(LocalDate date, Map<String, AccountState> accounts, PriceBook priceBook,
                           List<CashFlow> cashFlows,
                           List<ValidatorError> validationErrors) {

    public List<AccountState> getAccounts(String accountPattern) {
        return accounts.entrySet()
                .stream()
                .filter(e -> e.getKey().matches(accountPattern))
                .map(Map.Entry::getValue)
                .toList();
    }

    public Holdings getHoldings(String accountPattern) {
        return streamMatchingAccounts(accountPattern)
                .map(AccountState::getHoldings)
                .reduce(Holdings::add).get();

    }

    private Stream<AccountState> streamMatchingAccounts(String accountPattern) {
        return accounts.keySet().stream()
                .filter(accountId -> accountId.matches(accountPattern))
                .map(accounts::get);
    }

    public AccountState accountState() {
        return accounts.values().stream()
                .reduce(AccountState::merge)
                .orElseGet(AccountState::new);
    }
}
