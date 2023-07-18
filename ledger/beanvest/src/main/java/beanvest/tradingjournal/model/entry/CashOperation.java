package beanvest.tradingjournal.model.entry;

import java.math.BigDecimal;

public sealed interface CashOperation extends AccountOperation permits Transaction, Transfer {
    BigDecimal getCashAmount();

    String getCashCurrency();
}
