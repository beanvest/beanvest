package beanvest.processor.processingv2;

import beanvest.processor.pricebook.LatestPricesBook;
import beanvest.processor.processingv2.processor.AccountOpenDatesCollector;
import beanvest.processor.processingv2.processor.CashCalculator;
import beanvest.processor.processingv2.processor.CashflowCollector;
import beanvest.processor.processingv2.processor.CostMovedAtSaleCalculator;
import beanvest.processor.processingv2.processor.HoldingsCostCalculator;
import beanvest.processor.processingv2.processor.NetCostCalculator;
import beanvest.processor.processingv2.processor.DepositsCalculator;
import beanvest.processor.processingv2.processor.DividendCalculator;
import beanvest.processor.processingv2.processor.EarnedCalculator;
import beanvest.processor.processingv2.processor.FeesCalculator;
import beanvest.processor.processingv2.processor.HoldingsCollector;
import beanvest.processor.processingv2.processor.InterestCalculator;
import beanvest.processor.processingv2.processor.ProfitCalculator;
import beanvest.processor.processingv2.processor.periodic.PeriodCashCalculator;
import beanvest.processor.processingv2.processor.periodic.PeriodCashflowCollector;
import beanvest.processor.processingv2.processor.periodic.PeriodDepositCalculator;
import beanvest.processor.processingv2.processor.periodic.PeriodDividendCalculator;
import beanvest.processor.processingv2.processor.periodic.PeriodFeeCalculator;
import beanvest.processor.processingv2.processor.periodic.PeriodInterestCalculator;
import beanvest.processor.processingv2.processor.periodic.PeriodRealizedGainCalculator;
import beanvest.processor.processingv2.processor.periodic.PeriodUnrealizedGainCalculator;
import beanvest.processor.processingv2.processor.periodic.PeriodValueCalculator;
import beanvest.processor.processingv2.processor.periodic.PeriodWithdrawalCalculator;
import beanvest.processor.processingv2.processor.periodic.PeriodXirrCalculator;
import beanvest.processor.processingv2.processor.PlatformFeeCalculator;
import beanvest.processor.processingv2.processor.RealizedGainCalculator;
import beanvest.processor.processingv2.processor.SpentCalculator;
import beanvest.processor.processingv2.processor.TransactionFeeCalculator;
import beanvest.processor.processingv2.processor.UnrealizedGainCalculator;
import beanvest.processor.processingv2.processor.HoldingsValueCalculator;
import beanvest.processor.processingv2.processor.WithdrawalCalculator;
import beanvest.processor.processingv2.processor.XirrCalculator;

public class StatsCalculatorsRegistrar {
    public static ServiceRegistry registerDefaultCalculatorsFactories(ServiceRegistry registry) {
        registry.registerFactory(AccountOpenDatesCollector.class, reg -> new AccountOpenDatesCollector());
        registry.registerFactory(PlatformFeeCalculator.class, reg -> new PlatformFeeCalculator());
        registry.registerFactory(TransactionFeeCalculator.class, reg -> new TransactionFeeCalculator());
        registry.registerFactory(FeesCalculator.class, reg -> new FeesCalculator(reg.get(TransactionFeeCalculator.class), reg.get(PlatformFeeCalculator.class)));
        registry.registerFactory(PeriodFeeCalculator.class, reg -> new PeriodFeeCalculator(reg.get(FeesCalculator.class)));
        registry.registerFactory(InterestCalculator.class, reg -> new InterestCalculator());
        registry.registerFactory(PeriodInterestCalculator.class, reg -> new PeriodInterestCalculator(reg.get(InterestCalculator.class)));
        registry.registerFactory(DepositsCalculator.class, reg -> new DepositsCalculator());
        registry.registerFactory(PeriodDepositCalculator.class, reg -> new PeriodDepositCalculator(reg.get(DepositsCalculator.class)));
        registry.registerFactory(WithdrawalCalculator.class, reg -> new WithdrawalCalculator());
        registry.registerFactory(HoldingsCollector.class, reg -> new HoldingsCollector());
        registry.registerFactory(PeriodWithdrawalCalculator.class, reg -> new PeriodWithdrawalCalculator(reg.get(WithdrawalCalculator.class)));
        registry.registerFactory(RealizedGainCalculator.class, reg -> new RealizedGainCalculator());
        registry.registerFactory(PeriodRealizedGainCalculator.class, reg -> new PeriodRealizedGainCalculator(reg.get(RealizedGainCalculator.class)));
        registry.registerFactory(DividendCalculator.class, reg -> new DividendCalculator());
        registry.registerFactory(SpentCalculator.class, reg -> new SpentCalculator());
        registry.registerFactory(EarnedCalculator.class, reg -> new EarnedCalculator());
        registry.registerFactory(CashCalculator.class, reg -> new CashCalculator(reg.get(HoldingsCollector.class), reg.get(LatestPricesBook.class)));
        registry.registerFactory(PeriodCashCalculator.class, reg -> new PeriodCashCalculator(reg.get(CashCalculator.class)));
        registry.registerFactory(PeriodDividendCalculator.class, reg -> new PeriodDividendCalculator(reg.get(DividendCalculator.class)));
        registry.registerFactory(HoldingsValueCalculator.class, reg -> new HoldingsValueCalculator(reg.get(HoldingsCollector.class), reg.get(LatestPricesBook.class)));
        registry.registerFactory(PeriodValueCalculator.class, reg -> new PeriodValueCalculator(reg.get(ValueCalculator.class)));
        registry.registerFactory(UnrealizedGainCalculator.class, reg -> new UnrealizedGainCalculator(reg.get(HoldingsCollector.class), reg.get(HoldingsValueCalculator.class)));
        registry.registerFactory(PeriodUnrealizedGainCalculator.class, reg -> new PeriodUnrealizedGainCalculator(reg.get(UnrealizedGainCalculator.class)));
        registry.registerFactory(CashflowCollector.class, reg -> new CashflowCollector());
        registry.registerFactory(PeriodCashflowCollector.class, reg -> new PeriodCashflowCollector(reg.get(CashflowCollector.class)));
        registry.registerFactory(ValueCalculator.class, reg -> new ValueCalculator(reg.get(HoldingsValueCalculator.class), reg.get(CashCalculator.class)));
        registry.registerFactory(XirrCalculator.class, reg -> new XirrCalculator(
                reg.get(CashflowCollector.class),
                reg.get(HoldingsValueCalculator.class),
                reg.get(CashCalculator.class)));
        registry.registerFactory(PeriodXirrCalculator.class, reg -> new PeriodXirrCalculator(reg.get(PeriodCashflowCollector.class),
                reg.get(HoldingsValueCalculator.class), reg.get(CashCalculator.class)));
        registry.registerFactory(CostMovedAtSaleCalculator.class, reg -> new CostMovedAtSaleCalculator(reg.get(HoldingsCollector.class)));
        registry.registerFactory(HoldingsCostCalculator.class, reg -> new HoldingsCostCalculator(reg.get(HoldingsCollector.class)));
        registry.registerFactory(NetCostCalculator.class, reg -> new NetCostCalculator(reg.get(HoldingsCostCalculator.class)));
        registry.registerFactory(ProfitCalculator.class, reg -> new ProfitCalculator(reg.get(NetCostCalculator.class),
                reg.get(ValueCalculator.class)));

        return registry;
    }
}
