package beanvest.acceptance.returns.processingrework;

import beanvest.journal.Value;
import beanvest.journal.entry.Entry;
import beanvest.journal.entry.Fee;
import beanvest.parser.SourceLine;
import beanvest.processor.processing.AccountsResolver;
import beanvest.processor.processing.Grouping;
import beanvest.result.Result;
import beanvest.result.UserError;
import beanvest.result.UserErrors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class StatsStrategiesTest {
    private ServiceFactoryRegistry factoryRegistry;

    /*
    cases:
    - simple sum (eg fees (transaction, account service fee))
    - simple operations on other columns (value (cash + holdings value))
    - custom calculation for account, group, holding (xirr)
    - custom period calculation (xirr)
    - lack of value for type (last price, units)
     */

    @BeforeEach
    void setUp() {
        factoryRegistry = new ServiceFactoryRegistry();
    }

    @Test
    void shouldAllowAccountToSumUpValueFromHoldings() {
        var ar = new AccountsResolver(Grouping.WITH_GROUPS, true);
        factoryRegistry.register(FeeCollector.class, reg -> new FeeCollector(ar));
        factoryRegistry.instantiateServices();

        List<Entry> entries = List.of(
                fee("shares:fidelity", "1"),
                fee("shares:fidelity", "2"),
                fee("shares:vanguard", "4")
        );

        for (Entry entry : entries) {
            factoryRegistry.getProcessors().forEach(p -> p.process(entry));
        }

        var feeCollector = factoryRegistry.getOrCreate(FeeCollector.class);
        assertThat(feeCollector.get("shares:fidelity").getValue()).isEqualByComparingTo(new BigDecimal(3));
        assertThat(feeCollector.get("shares:vanguard").getValue()).isEqualByComparingTo(new BigDecimal(4));
        assertThat(feeCollector.get("shares").getValue()).isEqualByComparingTo(new BigDecimal(7));
    }

    private static Fee fee(String trading, String fee) {
        return new Fee(LocalDate.now(), trading, Value.of(new BigDecimal(fee), "GBP"), Optional.empty(), Optional.empty(), SourceLine.GENERATED_LINE);
    }

    public interface Processor {
        void process(Entry entry);
    }

    interface Calculator {
        Result<BigDecimal, UserErrors> calculate(final LocalDate endDate, String targetCurrency);
    }

    static class FeeCollector implements Processor {
        private final AccountsResolver ar;
        private Map<String, BigDecimal> balances = new HashMap<>();

        public FeeCollector(AccountsResolver ar) {
            this.ar = ar;
        }

        @Override
        public void process(Entry entry) {
            if (entry instanceof Fee op) {
                var newBalance = balances.getOrDefault(op.account(), BigDecimal.ZERO).add(op.getCashAmount());
                balances.put(op.account(), newBalance);
            }
        }

        public Result<BigDecimal, UserError> get(String account) {
            var result = BigDecimal.ZERO;
            for (String accountWithBalance : balances.keySet()) {
                if (accountWithBalance.startsWith(account)) {
                    result = result.add(balances.get(accountWithBalance));
                }
            }
            return Result.success(result);
        }
    }
}