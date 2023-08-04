package beanvest.processor.processingv2;

import beanvest.processor.processingv2.processor.DividendCalculator;
import beanvest.processor.processingv2.processor.FeesCalculator;
import beanvest.processor.processingv2.processor.InterestCalculator;
import beanvest.processor.processingv2.processor.PeriodFeeCalculator;
import beanvest.processor.processingv2.processor.PlatformFeeCalculator;
import beanvest.processor.processingv2.processor.PeriodDividendCalculator;
import beanvest.processor.processingv2.processor.TransactionFeeCalculator;

public class StatsCalculatorsRegistrar {
    public static ServiceRegistry registerDefaultCalculatorsFactories(ServiceRegistry serviceRegistry) {
        serviceRegistry.registerFactory(AccountOpenDatesCollector.class, reg -> new AccountOpenDatesCollector());
        serviceRegistry.registerFactory(PlatformFeeCalculator.class, reg -> new PlatformFeeCalculator());
        serviceRegistry.registerFactory(TransactionFeeCalculator.class, reg -> new TransactionFeeCalculator());
        serviceRegistry.registerFactory(FeesCalculator.class, reg -> new FeesCalculator(reg.get(TransactionFeeCalculator.class), reg.get(PlatformFeeCalculator.class)));
        serviceRegistry.registerFactory(PeriodFeeCalculator.class, reg -> new PeriodFeeCalculator(reg.get(FeesCalculator.class)));
        serviceRegistry.registerFactory(InterestCalculator.class, reg -> new InterestCalculator());
        serviceRegistry.registerFactory(PeriodInterestCalculator.class, reg -> new PeriodInterestCalculator(reg.get(InterestCalculator.class)));
        serviceRegistry.registerFactory(DividendCalculator.class, reg -> new DividendCalculator());
        serviceRegistry.registerFactory(PeriodDividendCalculator.class, reg -> new PeriodDividendCalculator(reg.get(DividendCalculator.class)));

        return serviceRegistry;
    }
}
