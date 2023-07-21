package beanvest.tradingjournal.processing;

import java.time.LocalDate;
import java.util.Optional;

public record AccountMetadata(
        LocalDate firstActivity,
        Optional<LocalDate> closingDate
) {
}
