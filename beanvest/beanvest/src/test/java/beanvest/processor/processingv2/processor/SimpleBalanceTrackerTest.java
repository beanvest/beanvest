package beanvest.processor.processingv2.processor;

import beanvest.journal.ConvertedValue;
import beanvest.journal.Value;
import beanvest.journal.entity.Account2;
import beanvest.journal.entity.Group;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleBalanceTrackerTest {

    public static final Group GROUP = Group.fromStringId("trading");
    public static final Account2 ACCOUNT1 = Account2.fromStringId("trading:acc1");
    public static final Account2 ACCOUNT2 = Account2.fromStringId("trading:acc2");
    private SimpleBalanceTracker tracker;

    @BeforeEach
    void setUp() {
        tracker = new SimpleBalanceTracker();
    }

    @Test
    void addingMainCurrencyProportionallyIncreasesConvertedValue() {
        tracker.add(GROUP, new ConvertedValue(Value.of("3 GBP"), Value.of("15 PLN")));
        tracker.add(GROUP, Value.of("2 GBP"));
        var calculate = tracker.calculate(GROUP, "GBP");
        assertThat(calculate.value()).isEqualTo(new BigDecimal(5));

        var calculateConverted = tracker.calculate(GROUP, "PLN");
        assertThat(calculateConverted.value()).isEqualTo(new BigDecimal(25));
    }

    @Test
    void addingWithDefinedConvertedValueAddsExcatlyThat() {
        tracker.add(GROUP, new ConvertedValue(Value.of("3 GBP"), Value.of("15 PLN")));
        tracker.add(GROUP, new ConvertedValue(Value.of("2 GBP"), Value.of("12 PLN")));
        var calculate = tracker.calculate(GROUP, "GBP");
        assertThat(calculate.value()).isEqualTo(new BigDecimal(5));

        var calculateConverted = tracker.calculate(GROUP, "PLN");
        assertThat(calculateConverted.value()).isEqualTo(new BigDecimal(27));
    }
}