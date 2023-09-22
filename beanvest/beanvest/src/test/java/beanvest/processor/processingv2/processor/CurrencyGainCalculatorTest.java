package beanvest.processor.processingv2.processor;

import beanvest.journal.entity.Account2;
import beanvest.parser.JournalParser;
import beanvest.processor.pricebook.LatestPricesBook;
import beanvest.processor.processingv2.AccountsTracker;
import beanvest.processor.processingv2.CalculationParams;
import beanvest.processor.processingv2.CurrencyConverterImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


class CurrencyGainCalculatorTest {

    private CurrencyGainCalculator calc;
    private LatestPricesBook pricesBook;

    @BeforeEach
    void setUp() {
        pricesBook = new LatestPricesBook();
        calc = new CurrencyGainCalculator(pricesBook);
    }

    @Test
    void currencyGainOnTopOfUnrealizedGain() {
        process("""
                account trading
                currency GBP
                2022-01-01 price GBP 5 PLN
                2022-01-01 deposit 1
                2022-01-02 buy 1 X for 1
                2022-01-03 price X 2 GBP
                2022-01-03 price GBP 6 PLN
                """);

        var calculated = calc.calculate(new CalculationParams(
                Account2.instrumentHolding("trading", "X", "GBP"),
                LocalDate.MIN,
                LocalDate.parse("2022-01-05"),
                "PLN"));

        assertThat(calculated.value()).isEqualByComparingTo(d("2"));
    }

    @Test
    void justCurrencyGain() {
        process("""
                account trading
                currency GBP
                2022-01-01 price GBP 5 PLN
                2022-01-01 deposit 1
                2022-01-02 buy 1 X for 1
                2022-01-03 price X 1 GBP
                2022-01-03 price GBP 6 PLN
                """);

        var calculated = calc.calculate(new CalculationParams(
                Account2.instrumentHolding("trading", "X", "GBP"),
                LocalDate.MIN,
                LocalDate.parse("2022-01-05"),
                "PLN"));

        assertThat(calculated.value()).isEqualByComparingTo(d("1"));
    }

    @Test
    void noGaine() {
        process("""
                account trading
                currency GBP
                2022-01-01 price GBP 5 PLN
                2022-01-01 deposit 1
                2022-01-02 buy 1 X for 1
                2022-01-03 price X 2 GBP
                """);

        var calculated = calc.calculate(new CalculationParams(
                Account2.instrumentHolding("trading", "X", "GBP"),
                LocalDate.MIN,
                LocalDate.parse("2022-01-05"),
                "PLN"));

        assertThat(calculated.value()).isEqualByComparingTo(d("0"));
    }

    @Test
    void noCurrencyGainJustUnrealizedGain() {
        process("""
                account trading
                currency GBP
                2022-01-01 price GBP 5 PLN
                2022-01-01 deposit 1
                2022-01-02 buy 1 X for 1
                2022-01-03 price X 2 GBP
                2022-01-03 price GBP 5 PLN
                """);

        var calculated = calc.calculate(new CalculationParams(
                Account2.instrumentHolding("trading", "X", "GBP"),
                LocalDate.MIN,
                LocalDate.parse("2022-01-05"),
                "PLN"));

        assertThat(calculated.value()).isEqualByComparingTo(d("0"));
    }


    @Test
    @Disabled("todo")
    void currencyGainOnCash() {
        process("""
                account trading
                currency GBP
                2022-01-01 price GBP 5 PLN
                2022-01-01 deposit 2
                2022-01-03 price GBP 6 PLN
                """);

        var calculated = calc.calculate(new CalculationParams(
                Account2.instrumentHolding("trading", "X", "GBP"),
                LocalDate.MIN,
                LocalDate.parse("2022-01-05"),
                "PLN"));

        assertThat(calculated.value()).isEqualByComparingTo(d("2"));
    }



    private BigDecimal d(String s) {
        return new BigDecimal(s);
    }

    private void process(String input) {
        var entries = new JournalParser().parse(input);
        var processor = new PrioritisedJournalEntryProcessor(pricesBook, new CurrencyConverterImpl("PLN", pricesBook), new AccountsTracker(),
                Set.of(calc), List.of());
        entries.getEntries().forEach(processor::process);
    }
}