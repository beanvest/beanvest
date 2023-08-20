package beanvest.processor.processingv2;

import beanvest.processor.pricebook.LatestPricesBook;
import beanvest.processor.processingv2.processor.*;
import beanvest.processor.processingv2.processor.periodic.*;
import beanvest.processor.processingv2.validator.BalanceValidator;
import beanvest.processor.processingv2.validator.AccountCloseValidator;

public class CalculatorRegistrar {
    public static void registerDefaultCalculatorsFactories(CalculatorRegistry registry) {
        registerCumulativeCalculators(registry);
        registerPeriodicCalculators(registry);
        registerValidators(registry);
    }

    private static void registerValidators(CalculatorRegistry registry) {
        registry.register(BalanceValidator.class, reg-> new BalanceValidator(reg.get(HoldingsCollector.class)));
        registry.register(AccountCloseValidator.class, reg-> new AccountCloseValidator(reg.get(HoldingsCollector.class)));
    }

    private static void registerPeriodicCalculators(CalculatorRegistry registry) {
        registry.register(PeriodFeeCalculator.class, reg -> new PeriodFeeCalculator(reg.get(FeesCalculator.class)));
        registry.register(PeriodInterestCalculator.class, reg -> new PeriodInterestCalculator(reg.get(InterestCalculator.class)));
        registry.register(PeriodDepositCalculator.class, reg -> new PeriodDepositCalculator(reg.get(DepositsCalculator.class)));
        registry.register(PeriodWithdrawalCalculator.class, reg -> new PeriodWithdrawalCalculator(reg.get(WithdrawalCalculator.class)));
        registry.register(PeriodRealizedGainCalculator.class, reg -> new PeriodRealizedGainCalculator(reg.get(RealizedGainCalculator.class)));
        registry.register(PeriodCashCalculator.class, reg -> new PeriodCashCalculator(reg.get(CashCalculator.class)));
        registry.register(PeriodDividendCalculator.class, reg -> new PeriodDividendCalculator(reg.get(DividendCalculator.class)));
        registry.register(PeriodValueCalculator.class, reg -> new PeriodValueCalculator(reg.get(ValueCalculator.class)));
        registry.register(PeriodUnrealizedGainCalculator.class, reg -> new PeriodUnrealizedGainCalculator(reg.get(UnrealizedGainCalculator.class)));
        registry.register(PeriodCashflowCollector.class, reg -> new PeriodCashflowCollector(reg.get(CashflowCollector.class)));
        registry.register(PeriodXirrCalculator.class, reg -> new PeriodXirrCalculator(reg.get(PeriodCashflowCollector.class),
                reg.get(HoldingsValueCalculator.class), reg.get(CashCalculator.class)));
        registry.register(PeriodDepositsPlusWithdrawalsCalculator.class, reg -> new PeriodDepositsPlusWithdrawalsCalculator(reg.get(DepositsPlusWithdrawalsCalculator.class)));
        registry.register(PeriodAccountGainCalculator.class, reg -> new PeriodAccountGainCalculator(reg.get(AccountGainCalculator.class)));
    }

    private static void registerCumulativeCalculators(CalculatorRegistry registry) {
        registry.register(AccountOpenDatesCollector.class, reg -> new AccountOpenDatesCollector());
        registry.register(PlatformFeeCalculator.class, reg -> new PlatformFeeCalculator());
        registry.register(TransactionFeeCalculator.class, reg -> new TransactionFeeCalculator());
        registry.register(FeesCalculator.class, reg -> new FeesCalculator(reg.get(TransactionFeeCalculator.class), reg.get(PlatformFeeCalculator.class)));
        registry.register(InterestCalculator.class, reg -> new InterestCalculator());
        registry.register(DepositsCalculator.class, reg -> new DepositsCalculator());
        registry.register(WithdrawalCalculator.class, reg -> new WithdrawalCalculator());
        registry.register(HoldingsCollector.class, reg -> new HoldingsCollector());
        registry.register(RealizedGainCalculator.class, reg -> new RealizedGainCalculator());
        registry.register(DividendCalculator.class, reg -> new DividendCalculator());
        registry.register(SpentCalculator.class, reg -> new SpentCalculator());
        registry.register(EarnedCalculator.class, reg -> new EarnedCalculator());
        registry.register(CashCalculator.class, reg -> new CashCalculator(reg.get(HoldingsCollector.class), reg.get(LatestPricesBook.class)));
        registry.register(HoldingsValueCalculator.class, reg -> new HoldingsValueCalculator(reg.get(HoldingsCollector.class), reg.get(LatestPricesBook.class)));
        registry.register(UnrealizedGainCalculator.class, reg -> new UnrealizedGainCalculator(reg.get(HoldingsCollector.class), reg.get(HoldingsValueCalculator.class)));
        registry.register(CashflowCollector.class, reg -> new CashflowCollector());
        registry.register(ValueCalculator.class, reg -> new ValueCalculator(reg.get(HoldingsValueCalculator.class), reg.get(CashCalculator.class)));
        registry.register(XirrCalculator.class, reg -> new XirrCalculator(
                reg.get(CashflowCollector.class),
                reg.get(HoldingsValueCalculator.class),
                reg.get(CashCalculator.class)));
        registry.register(CostMovedAtSaleCalculator.class, reg -> new CostMovedAtSaleCalculator(reg.get(HoldingsCollector.class)));
        registry.register(HoldingsCostCalculator.class, reg -> new HoldingsCostCalculator(reg.get(HoldingsCollector.class)));
        registry.register(NetCostCalculator.class, reg -> new NetCostCalculator(reg.get(HoldingsCostCalculator.class)));
        registry.register(ProfitCalculator.class, reg -> new ProfitCalculator(reg.get(NetCostCalculator.class),
                reg.get(ValueCalculator.class)));
        registry.register(DepositsPlusWithdrawalsCalculator.class, reg -> new DepositsPlusWithdrawalsCalculator(reg.get(DepositsCalculator.class),
                reg.get(WithdrawalCalculator.class)));
        registry.register(AccountGainCalculator.class, reg -> new AccountGainCalculator(
                reg.get(UnrealizedGainCalculator.class),
                reg.get(InterestCalculator.class),
                reg.get(DividendCalculator.class),
                reg.get(RealizedGainCalculator.class),
                reg.get(PlatformFeeCalculator.class)
        ));
    }
}
