package beanvest.journal.entry;

import beanvest.journal.entity.Account2;
import beanvest.parser.SourceLine;
import beanvest.journal.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public record Dividend(LocalDate date, Account2 account, Value value, String holdingSymbol, Optional<String> comment,
                       SourceLine originalLine) implements Transfer, HoldingOperation {
    @Override
    public String toJournalLine() {
        return date + " dividend " + " " + value.amount().toPlainString() + " " + value.symbol()
               + stringifyComment(comment);
    }

    @Override
    public Value getCashValue() {
        return value;
    }


    @Override
    public Dividend withValue(Value value) {
        return new Dividend(date, account, value, holdingSymbol, comment, originalLine);
    }

    @Override
    public BigDecimal getRawAmountMoved() {
        return getCashAmount();
    }
}
