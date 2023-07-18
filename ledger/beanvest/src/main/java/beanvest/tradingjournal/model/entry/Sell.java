package beanvest.tradingjournal.model.entry;

import beanvest.tradingjournal.SourceLine;
import beanvest.tradingjournal.model.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public record Sell(LocalDate date, String account, Value value, Value totalPrice,
                   BigDecimal fee, Optional<String> comment,
                   SourceLine originalLine) implements Transaction {
    @Override
    public String commodity() {
        return value.commodity();
    }

    @Override
    public BigDecimal units() {
        return value.amount();
    }

    @Override
    public String toJournalLine() {
        return date + " sell " + value.amount().toPlainString() + " " + value.commodity() + " for " + totalPrice
                + (fee.compareTo(BigDecimal.ZERO) != 0 ? " with fee " + fee.toPlainString() : "")
                + stringifyComment(comment);
    }

    @Override
    public BigDecimal getCashAmount() {
        return this.totalPrice.amount();
    }

    @Override
    public String getCashCurrency() {
        return this.totalPrice.commodity();
    }
}
