package beanvest.tradingjournal.model.entry;

import beanvest.tradingjournal.SourceLine;
import beanvest.tradingjournal.model.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public record Deposit(LocalDate date, String account, Value value,
                      Optional<String> comment, SourceLine originalLine) implements DepositOrWithdrawal, Transfer {
    @Override
    public String toJournalLine() {
        return date + " deposit " + value.amount().toPlainString() + " " + value().commodity()
                + stringifyComment(comment);
    }

    @Override
    public BigDecimal getRawAmountMoved() {
        return value.amount();
    }

    @Override
    public BigDecimal getCashAmount() {
        return value.amount();
    }

    @Override
    public String getCashCurrency() {
        return value.commodity();
    }
}
