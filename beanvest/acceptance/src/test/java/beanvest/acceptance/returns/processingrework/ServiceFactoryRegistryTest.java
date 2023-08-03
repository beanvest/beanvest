package beanvest.acceptance.returns.processingrework;

import beanvest.processor.pricebook.LatestPricesBook;
import beanvest.processor.processing.AccountType;
import beanvest.processor.processing.calculator.CashCalculator;
import beanvest.processor.processing.calculator.HoldingsValueCalculator;
import beanvest.processor.processing.calculator.TotalValueCalculator;
import beanvest.processor.processing.calculator.XirrPeriodicCalculator;
import beanvest.processor.processing.collector.DepositCollector;
import beanvest.processor.processing.collector.DividendCollector;
import beanvest.processor.processing.collector.EarnedCollector;
import beanvest.processor.processing.collector.HoldingsCollector;
import beanvest.processor.processing.collector.InterestCollector;
import beanvest.processor.processing.collector.PeriodCashFlowCollector;
import beanvest.processor.processing.collector.SimpleFeeCollector;
import beanvest.processor.processing.collector.SpentCollector;
import beanvest.processor.processing.collector.WithdrawalCollector;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ServiceFactoryRegistryTest {

    @Test
    void buildsServiceWithDependencies() {
        var latestPricesBook = new LatestPricesBook();

        var registry = new ServiceFactoryRegistry();
        registry.register(XirrPeriodicCalculator.class, serviceFactoryRegistry -> new XirrPeriodicCalculator(
                serviceFactoryRegistry.get(PeriodCashFlowCollector.class),
                serviceFactoryRegistry.get(TotalValueCalculator.class),
                serviceFactoryRegistry.get(HoldingsValueCalculator.class),
                AccountType.ACCOUNT
        ));
        registry.register(PeriodCashFlowCollector.class, reg -> new PeriodCashFlowCollector(AccountType.ACCOUNT));
        registry.register(TotalValueCalculator.class, reg -> new TotalValueCalculator(
                reg.get(HoldingsValueCalculator.class),
                reg.get(CashCalculator.class)));
        registry.register(TotalValueCalculator.class, reg -> new TotalValueCalculator(
                reg.get(HoldingsValueCalculator.class),
                reg.get(CashCalculator.class)));
        registry.register(HoldingsCollector.class,
                reg -> new HoldingsCollector());
        registry.register(HoldingsValueCalculator.class, reg -> new HoldingsValueCalculator(reg.get(HoldingsCollector.class), latestPricesBook));
        registry.register(CashCalculator.class, reg1 -> new CashCalculator(reg1.get(DepositCollector.class),
                reg1.get(WithdrawalCollector.class),
                reg1.get(InterestCollector.class),
                reg1.get(SimpleFeeCollector.class),
                reg1.get(DividendCollector.class),
                reg1.get(SpentCollector.class),
                reg1.get(EarnedCollector.class)));
        registry.register(DepositCollector.class, reg -> new DepositCollector());
        registry.register(WithdrawalCollector.class, reg -> new WithdrawalCollector());
        registry.register(InterestCollector.class, reg -> new InterestCollector());
        registry.register(SimpleFeeCollector.class, reg -> new SimpleFeeCollector());
        registry.register(DividendCollector.class, reg -> new DividendCollector());
        registry.register(SpentCollector.class, reg -> new SpentCollector());
        registry.register(EarnedCollector.class, reg -> new EarnedCollector());

        var xirrPeriodicCalculator = registry.get(XirrPeriodicCalculator.class);
        assertThat(xirrPeriodicCalculator).isInstanceOf(XirrPeriodicCalculator.class);
    }
}
