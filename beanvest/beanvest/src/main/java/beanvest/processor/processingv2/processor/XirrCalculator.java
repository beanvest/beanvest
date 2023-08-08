package beanvest.processor.processingv2.processor;

import beanvest.processor.processingv2.CashflowsXirrCalculator;
import beanvest.processor.processingv2.CalculationParams;
import beanvest.processor.processingv2.Calculator;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;

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
    public Result<BigDecimal, UserErrors> calculate(CalculationParams params) {
        var cashflows = cashflowCollector.getCashflows(params.entity());
        var maybeValue = holdingsValueCalculator.calculate(new CalculationParams(params.entity(), params.startDate(), params.endDate(), params.targetCurrency()))
                .combine(cashCalculator.calculate(new CalculationParams(params.entity(), params.startDate(), params.endDate(), params.targetCurrency())), BigDecimal::add, UserErrors::join);
        if (maybeValue.hasError()) {
            return maybeValue;
        }
        return cashflowsXirrCalculator.calculateXirr(params.endDate(), cashflows, maybeValue.value());
    }
}