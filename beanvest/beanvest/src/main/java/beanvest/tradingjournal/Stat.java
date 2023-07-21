package beanvest.tradingjournal;

import java.math.BigDecimal;
import java.util.Optional;

public record Stat(BigDecimal stat, Optional<BigDecimal> delta) {

}
