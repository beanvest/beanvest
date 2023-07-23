package beanvest.journal;

import java.time.LocalDate;

public record CashFlow(LocalDate date, Value transferredAmount) {
}
