package beanvest.export;

import java.math.BigDecimal;

public record Lot(BigDecimal price, BigDecimal units) {
}
