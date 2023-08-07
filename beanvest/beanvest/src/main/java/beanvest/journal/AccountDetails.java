package beanvest.journal;

import beanvest.processor.processingv2.Account2;

import java.time.LocalDate;
import java.util.Optional;

public record AccountDetails(Account2 account, java.util.Optional<String> currency, LocalDate openingDate,
                             Optional<LocalDate> closingDate) {
}
