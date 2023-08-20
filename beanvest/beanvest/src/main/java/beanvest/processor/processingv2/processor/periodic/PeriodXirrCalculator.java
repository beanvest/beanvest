package beanvest.processor.processingv2.processor.periodic;

import beanvest.processor.processingv2.processor.CashflowsXirrCalculator;
import beanvest.processor.processingv2.CalculationParams;
import beanvest.processor.processingv2.Calculator;
import beanvest.journal.entity.Entity;
import beanvest.processor.processingv2.processor.CashCalculator;
import beanvest.processor.processingv2.processor.HoldingsValueCalculator;
import beanvest.result.Result;
import beanvest.result.StatErrors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class PeriodXirrCalculator implements Calculator {

    private final CashflowsXirrCalculator cashflowsXirrCalculator;
    private final PeriodCashflowCollector periodCashflowCollector;
    private final HoldingsValueCalculator holdingsValueCalculator;
    private final CashCalculator cashCalculator;
    private final Map<DateEntity, Result<BigDecimal, StatErrors>> previousValues = new HashMap<>();

    public PeriodXirrCalculator(PeriodCashflowCollector periodCashflowCollector, HoldingsValueCalculator holdingsValueCalculator, CashCalculator cashCalculator) {
        this.holdingsValueCalculator = holdingsValueCalculator;
        this.cashCalculator = cashCalculator;
        this.cashflowsXirrCalculator = new CashflowsXirrCalculator();
        this.periodCashflowCollector = periodCashflowCollector;
    }

    @Override
    public Result<BigDecimal, StatErrors> calculate(CalculationParams params) {
        var cashFlows = periodCashflowCollector.getCashflows(params.entity(), params.startDate());
        var endValue = cashCalculator.calculate(params)
                .combine(holdingsValueCalculator.calculate(params), BigDecimal::add, StatErrors::join);
        var newKey = new DateEntity(params.endDate(), params.entity());
        previousValues.put(newKey, endValue);
        if (endValue.hasError()) {
            return endValue;
        }

        var prevKey = new DateEntity(getEndDateOfPerviousPeriod(params.startDate()), params.entity());
        var previous = previousValues.getOrDefault(prevKey, Result.success(BigDecimal.ZERO));
        if (previous.hasError()) {
            return previous;
        }
        return cashflowsXirrCalculator.calculateXirr(params.startDate(), previous.value(), params.endDate(), cashFlows, endValue.value());
    }

    private LocalDate getEndDateOfPerviousPeriod(LocalDate date) {
        return date.equals(LocalDate.MIN) ? LocalDate.MIN : date.minusDays(1);
    }

    record DateEntity(LocalDate date, Entity entity){}
}