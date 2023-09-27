package beanvest.processor.processingv2.processor;

import beanvest.journal.Value;
import beanvest.journal.entity.Account2;
import beanvest.journal.entity.AccountInstrumentHolding;
import beanvest.journal.entry.Buy;
import beanvest.journal.entry.Sell;
import beanvest.parser.SourceLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class AccountHoldingOpenDatesCollectorTest {

    public static final Account2 ACCOUNT = Account2.fromStringId("A/Shares:Isa");
    private AccountHoldingOpenDatesCollector collector;

    @BeforeEach
    void setUp() {
        collector = new AccountHoldingOpenDatesCollector();
    }

    @Test
    void storesClosingDateOfAHolding() {
        collector.process(buy("2023-01-01", "12 MSFT"));
        collector.process(sell("2023-01-02", "10 MSFT"));
        collector.process(buy("2023-01-03", "3 MSFT"));
        collector.process(sell("2023-01-04", "13 MSFT"));
        var holding = new AccountInstrumentHolding(ACCOUNT, "MSFT");
        var closingDate = collector.getClosingDate(holding).get();
        assertThat(closingDate).isEqualTo(LocalDate.parse("2023-01-04"));
    }

    @Test
    void hasNoClosingDateOfHoldingWasSoldOutAndRepurchased() {
        collector.process(buy("2023-01-01", "12 MSFT"));
        collector.process(sell("2023-01-02", "12 MSFT"));
        collector.process(buy("2023-01-03", "3 MSFT"));
        var holding = new AccountInstrumentHolding(ACCOUNT, "MSFT");
        var closingDate = collector.getClosingDate(holding);
        assertThat(closingDate).isEmpty();
    }

    @Test
    void storesOpeningDateOfAHolding() {
        collector.process(buy("2023-01-01", "12 MSFT"));
        collector.process(buy("2023-01-02", "13 MSFT"));
        collector.process(sell("2023-01-03", "10 MSFT"));
        var holding = new AccountInstrumentHolding(ACCOUNT, "MSFT");
        var date = collector.getFirstActivity(holding).get();
        assertThat(date).isEqualTo(LocalDate.parse("2023-01-01"));
    }

    @Test
    void storesOpeningDateOfAHoldingIgnoringSubsequentSellingOutAndRepurchasing() {
        collector.process(buy("2023-01-01", "12 MSFT"));
        collector.process(sell("2023-01-02", "12 MSFT"));
        collector.process(buy("2023-01-03", "12 MSFT"));
        var holding = new AccountInstrumentHolding(ACCOUNT, "MSFT");
        var date = collector.getFirstActivity(holding).get();
        assertThat(date).isEqualTo(LocalDate.parse("2023-01-01"));
    }


    private static Buy buy(String date, String valueString) {
        var totalPrice = Value.of("120 GBP");
        return new Buy(LocalDate.parse(date), ACCOUNT,
                Value.of(valueString), totalPrice, BigDecimal.ZERO, Optional.empty(), SourceLine.GENERATED_LINE);
    }

    private static Sell sell(String date, String valueString) {
        var totalPrice = Value.of("120 GBP");
        return new Sell(LocalDate.parse(date), ACCOUNT,
                Value.of(valueString), totalPrice, BigDecimal.ZERO, Optional.empty(), SourceLine.GENERATED_LINE);
    }
}