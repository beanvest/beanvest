package beanvest.test.processor.pricebook;

import beanvest.result.ErrorEnum;
import beanvest.result.UserErrors;
import beanvest.journal.Value;
import beanvest.journal.entry.Price;
import beanvest.processor.pricebook.LatestPricesBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LatestPricesBookTest {

    public static final LocalDate DATE_BEFORE_PRICING = LocalDate.parse("2020-01-01");
    private final LatestPricesBook priceBook = new LatestPricesBook();

    @BeforeEach
    void setUp() {
        priceBook.process(price("2021-01-01", "GBP", "5 PLN"));
        priceBook.process(price("2021-01-02", "GBP", "5.1 PLN"));
    }

    @Test
    void getsLatestPriceForTheDate() {

        var date = "2022-01-02";
        priceBook.process(price(date, "GBP", "5.2 PLN"));
        priceBook.process(price(date, "GBP", "5.3 PLN"));
        assertThat(priceBook.getPrice(LocalDate.parse(date), "GBP", "PLN").value())
                .usingRecursiveComparison().isEqualTo(Value.of("5.3 PLN"));
    }

    @Test
    void twoStepConversion() {
        var priceBook = new LatestPricesBook();
        priceBook.process(price("2021-01-01", "MSFT", "1 USD"));
        priceBook.process(price("2021-01-01", "USD", "4 PLN"));
        var converted = priceBook.convert(LocalDate.parse("2021-01-01"), "PLN", Value.of("1 MSFT"));
        assertThat(converted.value())
                .usingRecursiveComparison().isEqualTo(Value.of("4 PLN"));
    }

    @Test
    void returnsErrorIfPriceNotFound() {
        assertThatThrownBy(() -> priceBook.getPrice(DATE_BEFORE_PRICING, "GBP", "PLN"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void throwsIfPriceMoreThanWeeksOld() {
        assertThat(priceBook.getPrice(LocalDate.parse("2021-01-09"), "GBP", "PLN").value())
                .usingRecursiveComparison().isEqualTo(Value.of("5.1 PLN"));

        var result = priceBook.getPrice(LocalDate.parse("2021-01-10"), "GBP", "PLN");
        assertThat(result.error()).isInstanceOf(UserErrors.class);
        assertThat(result.error().getEnums()).isEqualTo(List.of(ErrorEnum.PRICE_NEEDED));
    }

    private static Price price(String dateString, String currency, String valueString) {
        return new Price(LocalDate.parse(dateString), currency, Value.of(valueString), Optional.empty(), null);
    }
}