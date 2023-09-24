package beanvest.journal.entry;

import beanvest.journal.entity.Account2;
import beanvest.parser.SourceLine;
import beanvest.journal.Value;
import beanvest.processor.processingv2.Holding;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public record Sell(LocalDate date, Account2 account, Value value, Value totalPrice,Value originalCurrencyTotalPrice,
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
    public Value getCashValue() {
        return totalPrice;
    }

    @Override
    public BigDecimal getRawAmountMoved() {
        return totalPrice.amount().negate();
    }

    public Sell withValue(Value newTotalPrice) {
        return new Sell(date, account, value, newTotalPrice, totalPrice, fee, comment, originalLine);
    }
}
