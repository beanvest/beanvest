package beanvest.processor.processingv2.processor.periodic;

import beanvest.processor.processingv2.CalculationParams;
import beanvest.processor.processingv2.Calculator;
import beanvest.journal.entity.Entity;
import beanvest.result.Result;
import beanvest.result.StatErrors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class SubtractingDeltaCalculator implements Calculator {
    private final Calculator calculator;

    private LocalDate endDate;
    private Map<Entity, Result<BigDecimal, StatErrors>> current = new HashMap<>();
    private Map<Entity, Result<BigDecimal, StatErrors>> previous = new HashMap<>();

    public SubtractingDeltaCalculator(Calculator calculator) {
        this.calculator = calculator;
    }

    @Override
    public final Result<BigDecimal, StatErrors> calculate(CalculationParams params) {
        if (!params.endDate().equals(this.endDate)) {
            previous = current;
            current = new HashMap<>();
            this.endDate = params.endDate();
        }
        var c = calculator.calculate(new CalculationParams(params.entity(), params.startDate(), params.endDate(), params.targetCurrency()));
        current.put(params.entity(), c);
        var p = previous.get(params.entity());
        if (p == null) {
            p = Result.success(BigDecimal.ZERO);
        }
        return c.combine(p, BigDecimal::subtract, StatErrors::join);
    }
}
