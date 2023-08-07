package beanvest.processor.processingv2;

import beanvest.processor.pricebook.LatestPricesBook;
import beanvest.processor.processingv2.processor.AccountOpenDatesCollector;
import beanvest.processor.processingv2.processor.CashCalculator;
import beanvest.processor.processingv2.processor.DepositsCalculator;
import beanvest.processor.processingv2.processor.DividendCalculator;
import beanvest.processor.processingv2.processor.EarnedCalculator;
import beanvest.processor.processingv2.processor.FeesCalculator;
import beanvest.processor.processingv2.processor.HoldingsCollector;
import beanvest.processor.processingv2.processor.InterestCalculator;
import beanvest.processor.processingv2.processor.PeriodCashCalculator;
import beanvest.processor.processingv2.processor.PeriodDepositCalculator;
import beanvest.processor.processingv2.processor.PeriodDividendCalculator;
import beanvest.processor.processingv2.processor.PeriodFeeCalculator;
import beanvest.processor.processingv2.processor.PeriodInterestCalculator;
import beanvest.processor.processingv2.processor.PeriodRealizedGainCalculator;
import beanvest.processor.processingv2.processor.PeriodUnrealizedGainCalculator;
import beanvest.processor.processingv2.processor.PeriodValueCalculator;
import beanvest.processor.processingv2.processor.PeriodWithdrawalCalculator;
import beanvest.processor.processingv2.processor.PlatformFeeCalculator;
import beanvest.processor.processingv2.processor.RealizedGainCalculator;
import beanvest.processor.processingv2.processor.SpentCalculator;
import beanvest.processor.processingv2.processor.TransactionFeeCalculator;
import beanvest.processor.processingv2.processor.UnrealizedGainCalculator;
import beanvest.processor.processingv2.processor.ValueCalculator;
import beanvest.processor.processingv2.processor.WithdrawalCalculator;

public class StatsCalculatorsRegistrar {
    public static ServiceRegistry registerDefaultCalculatorsFactories(ServiceRegistry serviceRegistry) {
        serviceRegistry.registerFactory(AccountOpenDatesCollector.class, reg -> new AccountOpenDatesCollector());
        serviceRegistry.registerFactory(PlatformFeeCalculator.class, reg -> new PlatformFeeCalculator());
        serviceRegistry.registerFactory(TransactionFeeCalculator.class, reg -> new TransactionFeeCalculator());
        serviceRegistry.registerFactory(FeesCalculator.class, reg -> new FeesCalculator(reg.get(TransactionFeeCalculator.class), reg.get(PlatformFeeCalculator.class)));
        serviceRegistry.registerFactory(PeriodFeeCalculator.class, reg -> new PeriodFeeCalculator(reg.get(FeesCalculator.class)));
        serviceRegistry.registerFactory(InterestCalculator.class, reg -> new InterestCalculator());
        serviceRegistry.registerFactory(PeriodInterestCalculator.class, reg -> new PeriodInterestCalculator(reg.get(InterestCalculator.class)));
        serviceRegistry.registerFactory(DepositsCalculator.class, reg -> new DepositsCalculator());
        serviceRegistry.registerFactory(PeriodDepositCalculator.class, reg -> new PeriodDepositCalculator(reg.get(DepositsCalculator.class)));
        serviceRegistry.registerFactory(WithdrawalCalculator.class, reg -> new WithdrawalCalculator());
        serviceRegistry.registerFactory(HoldingsCollector.class, reg -> new HoldingsCollector());
        serviceRegistry.registerFactory(PeriodWithdrawalCalculator.class, reg -> new PeriodWithdrawalCalculator(reg.get(WithdrawalCalculator.class)));
        serviceRegistry.registerFactory(RealizedGainCalculator.class, reg -> new RealizedGainCalculator());
        serviceRegistry.registerFactory(PeriodRealizedGainCalculator.class, reg -> new PeriodRealizedGainCalculator(reg.get(RealizedGainCalculator.class)));
        serviceRegistry.registerFactory(DividendCalculator.class, reg -> new DividendCalculator());
        serviceRegistry.registerFactory(SpentCalculator.class, reg -> new SpentCalculator());
        serviceRegistry.registerFactory(EarnedCalculator.class, reg -> new EarnedCalculator());
        serviceRegistry.registerFactory(CashCalculator.class, reg -> new CashCalculator(
                reg.get(DepositsCalculator.class),
                reg.get(WithdrawalCalculator.class),
                reg.get(InterestCalculator.class),
                reg.get(PlatformFeeCalculator.class),
                reg.get(DividendCalculator.class),
                reg.get(SpentCalculator.class),
                reg.get(EarnedCalculator.class)));
        serviceRegistry.registerFactory(PeriodCashCalculator.class, reg -> new PeriodCashCalculator(reg.get(CashCalculator.class)));
        serviceRegistry.registerFactory(PeriodDividendCalculator.class, reg -> new PeriodDividendCalculator(reg.get(DividendCalculator.class)));
        serviceRegistry.registerFactory(ValueCalculator.class, reg -> new ValueCalculator(reg.get(HoldingsCollector.class), reg.get(CashCalculator.class), reg.get(LatestPricesBook.class)));
        serviceRegistry.registerFactory(PeriodValueCalculator.class, reg -> new PeriodValueCalculator(reg.get(ValueCalculator.class)));
        serviceRegistry.registerFactory(UnrealizedGainCalculator.class, reg -> new UnrealizedGainCalculator(reg.get(HoldingsCollector.class), reg.get(ValueCalculator.class)));
        serviceRegistry.registerFactory(PeriodUnrealizedGainCalculator.class, reg -> new PeriodUnrealizedGainCalculator(reg.get(UnrealizedGainCalculator.class)));

        return serviceRegistry;
    }
}
