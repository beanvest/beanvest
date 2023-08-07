package beanvest.processor.processingv2.processor;

import beanvest.processor.processingv2.Calculator;
import beanvest.processor.processingv2.Entity;
import beanvest.result.ErrorFactory;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class SubtractingDeltaCalculator implements Calculator {
    private final Calculator calculator;

    private LocalDate endDate;
    private Map<Entity, Result<BigDecimal, UserErrors>> current = new HashMap<>();
    private Map<Entity, Result<BigDecimal, UserErrors>> previous = new HashMap<>();

    public SubtractingDeltaCalculator(Calculator calculator) {
        this.calculator = calculator;
    }

    @Override
    public final Result<BigDecimal, UserErrors> calculate(Entity entity, LocalDate endDate, String targetCurrency) {
        if (!endDate.equals(this.endDate)) {
            previous = current;
            current = new HashMap<>();
            this.endDate = endDate;
        }
        var c = calculator.calculate(entity, endDate, targetCurrency);
        current.put(entity, c);
        var p = previous.get(entity);
        if (p == null) {
            p = Result.success(BigDecimal.ZERO);
        }
        return c.combine(p, BigDecimal::subtract, UserErrors::join);
    }
}
