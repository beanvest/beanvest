package beanvest.processor.processingv2.dto;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

public record AccountDto2(String account, LocalDate openingDate, Optional<LocalDate> closingDate,
                          Map<String, StatsV2> periodStats) {
}
