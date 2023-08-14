package beanvest.scripts.usagegen.generatesamplejournal;

import java.math.BigDecimal;

public interface PriceBook {
    BigDecimal getPrice(String symbol);
}
