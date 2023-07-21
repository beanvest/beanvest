package beanvest.tradingjournal.model;

import java.time.LocalDate;
import java.util.Optional;

public record AccountDetails(String pattern, java.util.Optional<String> currency, LocalDate openingDate,
                             Optional<LocalDate> closingDate) {
}
