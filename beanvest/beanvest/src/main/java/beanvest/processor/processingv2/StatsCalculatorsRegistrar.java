package beanvest.processor.processingv2;

import beanvest.processor.processingv2.processor.DividendCalculator;
import beanvest.processor.processingv2.processor.PeriodDividendCalculator;

public class StatsCalculatorsRegistrar {
    public static ServiceRegistry registerDefaultCalculatorsFactories(ServiceRegistry serviceRegistry) {
        serviceRegistry.registerFactory(AccountOpenDatesCollector.class, reg -> new AccountOpenDatesCollector());
        serviceRegistry.registerFactory(DividendCalculator.class, reg -> new DividendCalculator());
        serviceRegistry.registerFactory(PeriodDividendCalculator.class, reg -> new PeriodDividendCalculator(reg.get(DividendCalculator.class)));

        return serviceRegistry;
    }
}
