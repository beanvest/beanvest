package beanvest.scripts.usagegen.generatesamplejournal.generator;

import java.math.BigDecimal;

public interface PriceBook {
    BigDecimal getPrice(String symbol);
}
