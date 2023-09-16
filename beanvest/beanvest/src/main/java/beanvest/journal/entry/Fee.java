package beanvest.journal.entry;

import beanvest.parser.SourceLine;
import beanvest.journal.Value;
import beanvest.journal.entity.Account2;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public record Fee(LocalDate date, Account2 account, Value value, Optional<String> holdingSymbol, Optional<String> comment,
                  SourceLine originalLine) implements Transfer {
    @Override
    public String toJournalLine() {
        return date + " fee " + " " + value.amount().toPlainString()
               + holdingSymbol.map(c -> " from " + c).orElse("")
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

    @Override
    public Fee withCashValue(Value value) {
        return new Fee(date, account, value, holdingSymbol, comment, originalLine);
    }

    @Override
    public BigDecimal getRawAmountMoved() {
        return getCashAmount().negate();
    }
}
