package beanvest.test.returns.unit;

import beanvest.tradingjournal.model.UserErrorId;
import beanvest.tradingjournal.model.UserErrors;
import beanvest.tradingjournal.model.Value;
import beanvest.tradingjournal.model.entry.Price;
import beanvest.tradingjournal.pricebook.PriceBook;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class PriceBookTest {

    private final PriceBook priceBook = new PriceBook(List.of(
            price("2021-01-01", "GBP", "5 PLN"),
            price("2021-01-02", "GBP", "5.1 PLN")
            ));

    @Test
    void getsLatestPriceForTheDate() {
        assertThat(priceBook.getPrice(LocalDate.parse("2021-01-05"), "GBP", "PLN").getValue())
                .usingRecursiveComparison().isEqualTo(Value.of("5.1 PLN"));

        assertThat(priceBook.getPrice(LocalDate.parse("2021-01-02"), "GBP", "PLN").getValue())
                .usingRecursiveComparison().isEqualTo(Value.of("5.1 PLN"));
        assertThat(priceBook.getPrice(LocalDate.parse("2021-01-01"), "GBP", "PLN").getValue())
                .usingRecursiveComparison().isEqualTo(Value.of("5 PLN"));
    }
    @Test
    void twoStepConversion() {
        var priceBook = new PriceBook(List.of(
                price("2021-01-01", "MSFT", "1 USD"),
                price("2021-01-01", "USD", "4 PLN")
        ));
        var converted = priceBook.convert(LocalDate.parse("2021-01-01"), "PLN", Value.of("1 MSFT"));
        assertThat(converted.getValue())
                .usingRecursiveComparison().isEqualTo(Value.of("4 PLN"));
    }

    @Test
    void returnsErrorIfPriceNotFound() {
        var result = priceBook.getPrice(LocalDate.parse("2020-01-01"), "GBP", "PLN");
        assertThat(result.getError()).isInstanceOf(UserErrors.class);
        assertThat(result.getError().getIds()).isEqualTo(List.of(UserErrorId.PRICE_NEEDED));
    }

    @Test
    void throwsIfPriceMoreThanWeeksOld() {
        assertThat(priceBook.getPrice(LocalDate.parse("2021-01-09"), "GBP", "PLN").getValue())
                .usingRecursiveComparison().isEqualTo(Value.of("5.1 PLN"));

        var result = priceBook.getPrice(LocalDate.parse("2021-01-10"), "GBP", "PLN");
        assertThat(result.getError()).isInstanceOf(UserErrors.class);
        assertThat(result.getError().getIds()).isEqualTo(List.of(UserErrorId.PRICE_NEEDED));
    }

    private static Price price(String dateString, String currency, String valueString) {
        return new Price(LocalDate.parse(dateString), currency, Value.of(valueString), Optional.empty(), null);
    }
}