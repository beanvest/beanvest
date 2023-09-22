package beanvest.processor.processingv2.processor;

import beanvest.journal.Value;
import beanvest.parser.JournalParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static beanvest.journal.entity.Account2.cashHolding;
import static beanvest.journal.entity.Account2.instrumentHolding;
import static org.assertj.core.api.Assertions.assertThat;

class HoldingsCollectorTest {
    private JournalParser parser;
    private HoldingsCollector collector;

    @BeforeEach
    void setUp() {
        parser = new JournalParser();
        collector = new HoldingsCollector();

    }

    @Test
    void keepsTrackOfHoldings() {
        process("""
                account trading
                currency GBP
                2022-01-01 deposit 22
                2022-01-02 buy 1 X for 10
                2022-01-02 buy 1 X for 12
                """);
        var holding = collector.getHolding(instrumentHolding("trading", "X"));

        assertThat(holding.asValue()).isEqualTo(Value.of("2 X"));
        assertThat(holding.totalCost()).isEqualByComparingTo(d("-22"));
        assertThat(holding.averageCost()).isEqualByComparingTo(d("-11"));
    }

    @Test
    void keepsTrackOfCash() {
        process("""
                account trading
                currency GBP
                2022-01-01 deposit 22
                2022-01-02 buy 1 X for 10
                """);
        var holding = collector.getHolding(cashHolding("trading", "GBP"));

        assertThat(holding.asValue()).isEqualTo(Value.of("12 GBP"));
        assertThat(holding.totalCost()).isEqualByComparingTo(d("-12"));
        assertThat(holding.averageCost()).isEqualByComparingTo(d("-1"));
    }

    private static BigDecimal d(String val) {
        return new BigDecimal(val);
    }

    private void process(String input) {
        var entries = parser.parse(input);

        entries.getAccountOperations()
                .forEach(e -> collector.process(e));
    }
}