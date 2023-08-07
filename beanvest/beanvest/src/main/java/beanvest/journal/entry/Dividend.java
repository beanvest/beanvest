package beanvest.journal.entry;

import beanvest.parser.SourceLine;
import beanvest.journal.Value;
import beanvest.processor.processingv2.AccountHolding;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public record Dividend(LocalDate date, beanvest.processor.processingv2.Account2 account2, Value value, String holdingSymbol, Optional<String> comment,
                       SourceLine originalLine) implements Transfer, HoldingOperation {
    @Override
    public String toJournalLine() {
        return date + " dividend " + " " + value.amount().toPlainString() + " " + value.symbol()
               + stringifyComment(comment);
    }

    @Override
    public BigDecimal getCashAmount() {
        return value.amount();
    }

    @Override
    public String getCashCurrency() {
        return value.symbol();
    }


}
