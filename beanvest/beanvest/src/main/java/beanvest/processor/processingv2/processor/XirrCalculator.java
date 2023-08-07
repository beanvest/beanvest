package beanvest.processor.processingv2.processor;

import beanvest.processor.processing.calculator.CashflowsXirrCalculator;
import beanvest.processor.processingv2.Calculator;
import beanvest.processor.processingv2.Entity;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.time.LocalDate;

public class XirrCalculator implements Calculator {

    private final CashflowsXirrCalculator cashflowsXirrCalculator;
    private final CashflowCollector cashflowCollector;
    private final HoldingsValueCalculator holdingsValueCalculator;
    private final CashCalculator cashCalculator;

    public XirrCalculator(CashflowCollector cashflowCollector, HoldingsValueCalculator holdingsValueCalculator, CashCalculator cashCalculator) {
        this.holdingsValueCalculator = holdingsValueCalculator;
        this.cashCalculator = cashCalculator;
        this.cashflowsXirrCalculator = new CashflowsXirrCalculator();
        this.cashflowCollector = cashflowCollector;
    }

    @Override
    public Result<BigDecimal, UserErrors> calculate(Entity entity, LocalDate endDate, String targetCurrency) {
        var cashflows = cashflowCollector.getCashflows(entity);
        var maybeValue = holdingsValueCalculator.calculate(entity, endDate, targetCurrency)
                .combine(cashCalculator.calculate(entity, endDate, targetCurrency), BigDecimal::add, UserErrors::join);
        if (maybeValue.hasError()) {
            return maybeValue;
        }
        return cashflowsXirrCalculator.calculateXirr(endDate, cashflows, maybeValue.value());
    }
}