package beanvest.journal.entry;

import beanvest.parser.SourceLine;
import beanvest.journal.Value;
import beanvest.journal.entity.Account2;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public record Buy(LocalDate date, Account2 account, Value value, Value totalPrice,
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
        return date + " buy " + value.amount().toPlainString() + " " + value.symbol() + " for " + totalPrice
                + (fee.compareTo(BigDecimal.ZERO) != 0 ? " with fee " + fee.toPlainString() : "")
                + stringifyComment(comment);
    }

    @Override
    public BigDecimal getRawAmountMoved() {
        return totalPrice.amount();
    }

    public Buy withValue(Value totalPrice) {
        return new Buy(date, account, value, totalPrice, fee, comment, originalLine);
    }

    @Override
    public Value getCashValue() {
        return totalPrice;
    }
}
