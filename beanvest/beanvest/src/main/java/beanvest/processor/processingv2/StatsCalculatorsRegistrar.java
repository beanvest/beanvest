package beanvest.processor.processingv2;

import beanvest.processor.processingv2.processor.DividendCollector;
import beanvest.processor.processingv2.processor.PeriodDividendCollector;

public class StatsCalculatorsRegistrar {
    public static ServiceRegistry registerDefaultCalculatorsFactories(ServiceRegistry serviceRegistry) {
        serviceRegistry.registerFactory(DividendCollector.class, reg -> new DividendCollector());
        serviceRegistry.registerFactory(PeriodDividendCollector.class, reg -> new PeriodDividendCollector(reg.get(DividendCollector.class)));

        return serviceRegistry;
    }
}
