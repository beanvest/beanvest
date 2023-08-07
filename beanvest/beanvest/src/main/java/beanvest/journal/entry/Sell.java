package beanvest.journal.entry;

import beanvest.parser.SourceLine;
import beanvest.journal.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public record Sell(LocalDate date, beanvest.processor.processingv2.Account2 account2, Value value, Value totalPrice,
                   BigDecimal fee, Optional<String> comment,
                   SourceLine originalLine) implements Transaction, HoldingOperation {
    @Override
    public String holdingSymbol() {
        return value.symbol();
    }

    @Override
    public BigDecimal units() {
        return value.amount();
    }

    @Override
    public String toJournalLine() {
        return date + " sell " + value.amount().toPlainString() + " " + value.symbol() + " for " + totalPrice
               + (fee.compareTo(BigDecimal.ZERO) != 0 ? " with fee " + fee.toPlainString() : "")
               + stringifyComment(comment);
    }

    @Override
    public BigDecimal getCashAmount() {
        return this.totalPrice.amount();
    }

    @Override
    public String getCashCurrency() {
        return this.totalPrice.symbol();
    }

    @Override
    public BigDecimal getRawAmountMoved() {
        return totalPrice.amount().negate();
    }
}
