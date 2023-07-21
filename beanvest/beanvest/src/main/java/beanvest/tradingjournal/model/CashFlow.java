package beanvest.tradingjournal.model;

import java.time.LocalDate;

public record CashFlow(LocalDate date, Value transferredAmount) {
}
