package beanvest.test.tradingjournal.model.entry;

import beanvest.test.tradingjournal.SourceLine;
import beanvest.test.tradingjournal.model.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public record Withdrawal(LocalDate date, String account, Value value,
                         Optional<String> comment, SourceLine originalLine) implements DepositOrWithdrawal, Transfer {
    @Override
    public String toJournalLine() {
        return date + " withdraw " + value.amount() + " " + value.commodity()
                + stringifyComment(comment);
    }


    @Override
    public BigDecimal getRawAmountMoved() {
        return value.amount().negate();
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
