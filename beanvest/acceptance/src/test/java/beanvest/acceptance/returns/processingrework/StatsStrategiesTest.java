package beanvest.acceptance.returns.processingrework;

import beanvest.journal.Value;
import beanvest.journal.entry.Buy;
import beanvest.journal.entry.Entry;
import beanvest.journal.entry.Fee;
import beanvest.journal.entry.Interest;
import beanvest.parser.SourceLine;
import beanvest.processor.processing.AccountType;
import beanvest.processor.processing.Grouping;
import beanvest.result.ErrorFactory;
import beanvest.result.Result;
import beanvest.result.UserErrors;
import org.assertj.core.api.SoftAssertions;
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
    ✓ simple sum (eg fees (transaction, account service fee))
    ✓ simple operations on other columns (value (cash + holdings value))
    ✓ lack of value for type (last price, units)
    - custom calculation for account, group, holding (xirr)
    - custom period calculation (xirr)
     */

    @BeforeEach
    void setUp() {
        factoryRegistry = new ServiceFactoryRegistry();
    }

    @Test
    void shouldAllowNotReturningValuesForSomeAccountTypes() {
        factoryRegistry.register(AccountsResolver2.class, reg -> new AccountsResolver2(Grouping.WITH_GROUPS, true));
        factoryRegistry.register(LatestPriceChecker.class, reg -> new LatestPriceChecker(reg.getOrCreate(AccountsResolver2.class)));
        factoryRegistry.instantiateServices();

        List<Entry> entries = List.of(
                buy("shares:fidelity", "APPL", "10"),
                buy("shares:fidelity", "MSFT", "20")
        );

        for (Entry entry : entries) {
            factoryRegistry.getProcessors().forEach(p -> p.process(entry));
        }

        var feeCollector = factoryRegistry.getOrCreate(LatestPriceChecker.class);
        assertThat(feeCollector.calculate("shares:fidelity:APPL", LocalDate.now(), "GBP").value())
                .isEqualByComparingTo(new BigDecimal(10));

        assertThat(feeCollector.calculate("shares:fidelity", LocalDate.now(), "GBP"))
                .isEqualTo(Result.failure(ErrorFactory.disabledForAccountType()));
        assertThat(feeCollector.calculate("shares:.*", LocalDate.now(), "GBP"))
                .isEqualTo(Result.failure(ErrorFactory.disabledForAccountType()));
        assertThat(feeCollector.calculate(".*", LocalDate.now(), "GBP"))
                .isEqualTo(Result.failure(ErrorFactory.disabledForAccountType()));
    }

    private Entry buy(String s, String symbol, String number) {
        return new Buy(LocalDate.now(), s, Value.of(number, symbol), Value.of(number, "GBP"),
                BigDecimal.ZERO, Optional.empty(), SourceLine.GENERATED_LINE);
    }

    static class LatestPriceChecker implements Processor, Calculator {
        private final AccountsResolver2 accountsResolver;
        private int retuned = 0;

        public LatestPriceChecker(AccountsResolver2 accountsResolver) {
            this.accountsResolver = accountsResolver;
        }

        @Override
        public void process(Entry entry) {
        }

        @Override
        public Result<BigDecimal, UserErrors> calculate(String account, LocalDate endDate, String targetCurrency) {
            var acc = accountsResolver.findKnownAccount(account).get();

            if (acc.type() == AccountType.HOLDING) {
                retuned += 1;
                return Result.success(new BigDecimal(retuned * 10));
            } else {
                return Result.failure(ErrorFactory.disabledForAccountType());
            }
        }
    }

    @Test
    void shouldAllowAccountToSumUpValueFromHoldings() {
        factoryRegistry.register(FeeCollector.class, reg -> new FeeCollector());
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
        assertThat(feeCollector.calculate("shares:fidelity", LocalDate.now(), "GBP").value()).isEqualByComparingTo(new BigDecimal(-3));
        assertThat(feeCollector.calculate("shares:vanguard", LocalDate.now(), "GBP").value()).isEqualByComparingTo(new BigDecimal(-4));
        assertThat(feeCollector.calculate("shares", LocalDate.now(), "GBP").value()).isEqualByComparingTo(new BigDecimal(-7));
    }

    @Test
    void shouldHandleCalculationsBasedOnOtherColumns() {
        factoryRegistry.register(FeeCollector.class, reg -> new FeeCollector());
        factoryRegistry.register(InterestCollector.class, reg -> new InterestCollector());
        factoryRegistry.register(FeePlusInterestCalculator.class, reg -> new FeePlusInterestCalculator(
                reg.getOrCreate(FeeCollector.class),
                reg.getOrCreate(InterestCollector.class)));

        factoryRegistry.instantiateServices();

        List<Entry> entries = List.of(
                fee("shares:fidelity", "1"),
                fee("shares:fidelity", "2"),
                fee("shares:vanguard", "4"),
                interest("shares:fidelity", "8"),
                interest("shares:fidelity", "16"),
                interest("shares:vanguard", "32")
        );

        for (Entry entry : entries) {
            factoryRegistry.getProcessors().forEach(p -> p.process(entry));
        }

        var calc = factoryRegistry.getOrCreate(FeePlusInterestCalculator.class);
        SoftAssertions.assertSoftly(a -> {
            a.assertThat(calc.calculate("shares:fidelity", LocalDate.now(), "GBP").value())
                    .isEqualByComparingTo(new BigDecimal(21));
            a.assertThat(calc.calculate("shares:vanguard", LocalDate.now(), "GBP").value())
                    .isEqualByComparingTo(new BigDecimal(28));
            a.assertThat(calc.calculate("shares", LocalDate.now(), "GBP").value())
                    .isEqualByComparingTo(new BigDecimal(49));
        });
    }

    private static Fee fee(String trading, String fee) {
        return new Fee(LocalDate.now(), trading, Value.of(new BigDecimal(fee), "GBP"), Optional.empty(), Optional.empty(), SourceLine.GENERATED_LINE);
    }

    private static Interest interest(String trading, String interest) {
        return new Interest(LocalDate.now(), trading, Value.of(new BigDecimal(interest), "GBP"), Optional.empty(), SourceLine.GENERATED_LINE);
    }

    public interface Processor {
        void process(Entry entry);
    }

    interface Calculator {
        Result<BigDecimal, UserErrors> calculate(final String account, final LocalDate endDate, String targetCurrency);
    }

    static class FeePlusInterestCalculator implements Calculator {
        private final FeeCollector feeCollector;
        private final InterestCollector interestCollector;

        public FeePlusInterestCalculator(FeeCollector feeCollector, InterestCollector interestCollector) {
            this.feeCollector = feeCollector;
            this.interestCollector = interestCollector;
        }


        @Override
        public Result<BigDecimal, UserErrors> calculate(String account, LocalDate endDate, String targetCurrency) {
            return feeCollector.calculate(account, endDate, targetCurrency)
                    .combine(interestCollector.calculate(account, endDate, targetCurrency), BigDecimal::add, UserErrors::join);
        }
    }

    static class FeeCollector implements Processor, Calculator {
        private final Map<String, BigDecimal> balances = new HashMap<>();

        public FeeCollector() {
        }

        @Override
        public void process(Entry entry) {
            if (entry instanceof Fee op) {
                var newBalance = balances.getOrDefault(op.account(), BigDecimal.ZERO).subtract(op.getCashAmount());
                balances.put(op.account(), newBalance);
            }
        }

        @Override
        public Result<BigDecimal, UserErrors> calculate(String account, LocalDate endDate, String targetCurrency) {
            var result = BigDecimal.ZERO;
            for (String accountWithBalance : balances.keySet()) {
                if (accountWithBalance.startsWith(account)) {
                    result = result.add(balances.get(accountWithBalance));
                }
            }
            return Result.success(result);
        }
    }

    static class InterestCollector implements Processor, Calculator {
        private final Map<String, BigDecimal> balances = new HashMap<>();

        public InterestCollector() {
        }

        @Override
        public void process(Entry entry) {
            if (entry instanceof Interest op) {
                var newBalance = balances.getOrDefault(op.account(), BigDecimal.ZERO).add(op.getCashAmount());
                balances.put(op.account(), newBalance);
            }
        }

        @Override
        public Result<BigDecimal, UserErrors> calculate(String account, LocalDate endDate, String targetCurrency) {
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