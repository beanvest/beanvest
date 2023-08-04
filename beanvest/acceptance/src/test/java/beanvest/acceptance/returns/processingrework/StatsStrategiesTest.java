package beanvest.acceptance.returns.processingrework;

import beanvest.journal.Value;
import beanvest.journal.entry.Buy;
import beanvest.journal.entry.Entry;
import beanvest.journal.entry.Fee;
import beanvest.journal.entry.Interest;
import beanvest.parser.SourceLine;
import beanvest.processor.processing.Grouping;
import beanvest.result.ErrorFactory;
import beanvest.result.Result;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class StatsStrategiesTest {
    private ServiceFactoryRegistry factoryRegistry;

    /*
    cases:
    ✓ simple sum (eg fees (transaction, account service fee))
    ✓ simple operations on other columns (value (cash + holdings value))
    ✓ lack of value for type (last price, units)
        ✓ custom calculation for account, group, holding (xirr)
    ✓ custom period calculation (xirr) (might need simple subtracting wrapper for regular stats' deltas)
        ✓ regular delta should be easy to do
     */

    @BeforeEach
    void setUp() {
        factoryRegistry = new ServiceFactoryRegistry();
    }

    @Test
    void shouldAllowCustomCalculationForPeriod() {
        factoryRegistry.register(FeeCollector.class, reg -> new FeeCollector());
        factoryRegistry.register(PeriodFeeCollector.class, reg -> new PeriodFeeCollector(reg.get(FeeCollector.class)));
        factoryRegistry.instantiateServices();

        var entry1 = fee("2020-01-01", "fidelity", "9");
        var entry2 = fee("2021-01-01", "fidelity", "10");
        var entry3 = fee("2022-01-01", "fidelity", "11");
        var calc = factoryRegistry.get(PeriodFeeCollector.class);

        factoryRegistry.getProcessors().forEach(p -> p.process(entry1));
        calc.calculate("fidelity", LocalDate.parse("2020-12-31"), "GBP");

        factoryRegistry.getProcessors().forEach(p -> p.process(entry2));
        assertThat(calc.calculate("fidelity", LocalDate.parse("2021-12-31"), "GBP").value())
                .isEqualByComparingTo(new BigDecimal(-10));

        factoryRegistry.getProcessors().forEach(p -> p.process(entry3));
        assertThat(calc.calculate("fidelity", LocalDate.parse("2022-12-31"), "GBP").value())
                .isEqualByComparingTo(new BigDecimal(-11));

        var calc2 = factoryRegistry.get(FeeCollector.class);

        assertThat(calc2.calculate("fidelity", LocalDate.parse("2022-12-31"), "GBP").value())
                .isEqualByComparingTo(new BigDecimal(-30));
    }

    @Test
    void shouldAllowNotReturningValuesForSomeAccountTypes() {
        factoryRegistry.register(AccountsResolver2.class, reg -> new AccountsResolver2(Grouping.WITH_GROUPS, true));
        factoryRegistry.register(LatestPriceChecker.class, reg -> new LatestPriceChecker(reg.get(AccountsResolver2.class)));
        factoryRegistry.instantiateServices();

        List<Entry> entries = List.of(
                buy("shares:fidelity", "APPL", "10"),
                buy("shares:fidelity", "MSFT", "20")
        );

        for (Entry entry : entries) {
            factoryRegistry.getProcessors().forEach(p -> p.process(entry));
        }

        var feeCollector = factoryRegistry.get(LatestPriceChecker.class);
        assertThat(feeCollector.calculate("shares:fidelity:APPL", LocalDate.now(), "GBP").value())
                .isEqualByComparingTo(new BigDecimal(10));

        assertThat(feeCollector.calculate("shares:fidelity", LocalDate.now(), "GBP"))
                .isEqualTo(Result.failure(ErrorFactory.disabledForAccountType()));
        assertThat(feeCollector.calculate("shares:.*", LocalDate.now(), "GBP"))
                .isEqualTo(Result.failure(ErrorFactory.disabledForAccountType()));
        assertThat(feeCollector.calculate(".*", LocalDate.now(), "GBP"))
                .isEqualTo(Result.failure(ErrorFactory.disabledForAccountType()));
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

        var feeCollector = factoryRegistry.get(FeeCollector.class);
        assertThat(feeCollector.calculate("shares:fidelity", LocalDate.now(), "GBP").value()).isEqualByComparingTo(new BigDecimal(-3));
        assertThat(feeCollector.calculate("shares:vanguard", LocalDate.now(), "GBP").value()).isEqualByComparingTo(new BigDecimal(-4));
        assertThat(feeCollector.calculate("shares", LocalDate.now(), "GBP").value()).isEqualByComparingTo(new BigDecimal(-7));
    }

    @Test
    void shouldHandleCalculationsBasedOnOtherColumns() {
        factoryRegistry.register(FeeCollector.class, reg -> new FeeCollector());
        factoryRegistry.register(InterestCollector.class, reg -> new InterestCollector());
        factoryRegistry.register(FeePlusInterestCalculator.class, reg -> new FeePlusInterestCalculator(
                reg.get(FeeCollector.class),
                reg.get(InterestCollector.class)));

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

        var calc = factoryRegistry.get(FeePlusInterestCalculator.class);
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
        return fee(LocalDate.now().toString(), trading, fee);
    }

    private static Fee fee(String date, String trading, String fee) {
        return new Fee(LocalDate.parse(date), trading, Value.of(new BigDecimal(fee), "GBP"), Optional.empty(), Optional.empty(), SourceLine.GENERATED_LINE);
    }

    private Entry buy(String account, String symbol, String number) {
        return buy(LocalDate.now().toString(), account, symbol, number);
    }

    private static Interest interest(String trading, String interest) {
        return new Interest(LocalDate.now(), trading, Value.of(new BigDecimal(interest), "GBP"), Optional.empty(), SourceLine.GENERATED_LINE);
    }

    private Buy buy(String date, String account, String symbol, String number) {
        return new Buy(LocalDate.parse(date), account, Value.of(number, symbol), Value.of(number, "GBP"),
                BigDecimal.ZERO, Optional.empty(), SourceLine.GENERATED_LINE);
    }

}