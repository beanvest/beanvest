package beanvest.processor.dto;

import java.math.BigDecimal;
import java.util.Optional;

public record StatDto(BigDecimal stat, Optional<BigDecimal> delta) {

}
