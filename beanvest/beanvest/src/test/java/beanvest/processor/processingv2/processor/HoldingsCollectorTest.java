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



    private static BigDecimal d(String val) {
        return new BigDecimal(val);
    }

    private void process(String input) {
        var entries = parser.parse(input);

        entries.getAccountOperations()
                .forEach(e -> collector.process(e));
    }
}