package beanvest.processor.processingv2.processor;

import beanvest.processor.processing.calculator.CashflowsXirrCalculator;
import beanvest.processor.processingv2.CalculationParams;
import beanvest.processor.processingv2.Calculator;
import beanvest.processor.processingv2.Entity;
import beanvest.result.ErrorFactory;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class PeriodXirrCalculator implements Calculator {

    private final CashflowsXirrCalculator cashflowsXirrCalculator;
    private final PeriodCashflowCollector periodCashflowCollector;
    private final HoldingsValueCalculator holdingsValueCalculator;
    private final CashCalculator cashCalculator;
    private final Map<DateEntity, Result<BigDecimal, UserErrors>> previousValues = new HashMap<>();

    public PeriodXirrCalculator(PeriodCashflowCollector periodCashflowCollector, HoldingsValueCalculator holdingsValueCalculator, CashCalculator cashCalculator) {
        this.holdingsValueCalculator = holdingsValueCalculator;
        this.cashCalculator = cashCalculator;
        this.cashflowsXirrCalculator = new CashflowsXirrCalculator();
        this.periodCashflowCollector = periodCashflowCollector;
    }

    @Override
    public Result<BigDecimal, UserErrors> calculate(CalculationParams params) {
        var cashflows = periodCashflowCollector.getCashflows(params.entity(), params.startDate());
        var endValue = cashCalculator.calculate(params)
                .combine(holdingsValueCalculator.calculate(params), BigDecimal::add, UserErrors::join);
        var newKey = new DateEntity(params.endDate(), params.entity());
        previousValues.put(newKey, endValue);
        if (endValue.hasError()) {
            return endValue;
        }


        var prevKey = new DateEntity(params.startDate().minusDays(1), params.entity());
        var previous = previousValues.getOrDefault(prevKey, Result.success(BigDecimal.ZERO));
        if (previous.hasError()) {
            return previous;
        }
        var result = cashflowsXirrCalculator.calculateXirr(params.startDate(), previous.value(), params.endDate(), cashflows, endValue.value());
        return result;
    }

    record DateEntity(LocalDate date, Entity entity){}
}