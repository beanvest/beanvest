package beanvest.acceptance.returns.processingrework;

import beanvest.processor.processing.calculator.StatCalculator;

public interface StatCalculatorFactory {
    StatCalculator build(ServiceFactoryRegistry serviceFactoryRegistry);
}
