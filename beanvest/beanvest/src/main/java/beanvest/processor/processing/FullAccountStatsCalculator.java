package beanvest.processor.processing;

import beanvest.processor.ValueStatsDto;
import beanvest.journal.entry.Entry;
import beanvest.processor.processing.calculator.AccountGainCalculator;
import beanvest.processor.processing.calculator.AccountValueCalculator;
import beanvest.processor.processing.calculator.TotalFeesCalculator;
import beanvest.processor.processing.calculator.TotalValueCalculator;
import beanvest.processor.processing.collector.DepositCollector;
import beanvest.processor.processing.collector.DividendCollector;
import beanvest.processor.processing.collector.EarnedCollector;
import beanvest.processor.processing.collector.TransactionFeeCollector;
import beanvest.journal.CashStats;
import beanvest.journal.Stats;
import beanvest.processor.processing.calculator.XirrCalculator;
import beanvest.processor.pricebook.LatestPricesBook;
import beanvest.processor.processing.calculator.HoldingsCostCalculator;
import beanvest.processor.processing.calculator.HoldingsValueCalculator;
import beanvest.processor.processing.calculator.UnrealizedGainsCalculator;
import beanvest.processor.processing.collector.AccountOpenDatesCollector;
import beanvest.processor.processing.calculator.CashCalculator;
import beanvest.processor.processing.collector.FullCashFlowCollector;
import beanvest.processor.processing.collector.HoldingsCollector;
import beanvest.processor.processing.collector.InterestCollector;
import beanvest.processor.processing.collector.RealizedGainsCollector;
import beanvest.processor.processing.collector.SimpleFeeCollector;
import beanvest.processor.processing.collector.SpentCollector;
import beanvest.processor.processing.collector.WithdrawalCollector;

import java.time.LocalDate;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class FullAccountStatsCalculator implements Collector {
    private final AccountOpenDatesCollector accountOpenDatesCollector = new AccountOpenDatesCollector();
    private final DepositCollector depositCollector = new DepositCollector();
    private final DividendCollector dividendCollector = new DividendCollector();
    private final EarnedCollector earnedCollector = new EarnedCollector();
    private final FullCashFlowCollector fullCashFlowCollector = new FullCashFlowCollector();
    private final HoldingsCollector holdingsCollector = new HoldingsCollector();
    private final InterestCollector interestCollector = new InterestCollector();
    private final RealizedGainsCollector realizedGainsCollector = new RealizedGainsCollector();
    private final SimpleFeeCollector simpleFeeCollector = new SimpleFeeCollector();
    private final SpentCollector spentCollector = new SpentCollector();
    private final TransactionFeeCollector transactionFeeCollector = new TransactionFeeCollector();
    private final WithdrawalCollector withdrawalsCollector = new WithdrawalCollector();

    private final AccountGainCalculator accountGainCalculator;
    private final AccountValueCalculator accountValueCalculator;
    private final CashCalculator cashCalculator;
    private final HoldingsCostCalculator holdingsCostCalculator;
    private final HoldingsValueCalculator holdingsValueCalculator;
    private final TotalFeesCalculator totalFeesCalculator;
    private final TotalValueCalculator totalValueCalculator;
    private final UnrealizedGainsCalculator unrealizedGainsCalculator;
    private final XirrCalculator xirrCalculator;
    private final List<Collector> collectors = List.of(
            accountOpenDatesCollector,
            depositCollector,
            dividendCollector,
            earnedCollector,
            fullCashFlowCollector,
            holdingsCollector,
            interestCollector,
            realizedGainsCollector,
            simpleFeeCollector,
            spentCollector,
            transactionFeeCollector,
            withdrawalsCollector
    );

    public FullAccountStatsCalculator(LatestPricesBook pricesBook) {
        holdingsValueCalculator = new HoldingsValueCalculator(holdingsCollector, pricesBook);
        holdingsCostCalculator = new HoldingsCostCalculator(holdingsCollector);
        unrealizedGainsCalculator = new UnrealizedGainsCalculator(holdingsValueCalculator, holdingsCostCalculator);
        cashCalculator = new CashCalculator(
                depositCollector, withdrawalsCollector, interestCollector,
                simpleFeeCollector, dividendCollector, spentCollector, earnedCollector);
        totalValueCalculator = new TotalValueCalculator(holdingsValueCalculator, cashCalculator);
        xirrCalculator = new XirrCalculator(fullCashFlowCollector, totalValueCalculator);
        accountValueCalculator = new AccountValueCalculator(holdingsValueCalculator, cashCalculator);
        accountGainCalculator = new AccountGainCalculator(depositCollector, withdrawalsCollector, accountValueCalculator);
        totalFeesCalculator = new TotalFeesCalculator(simpleFeeCollector, transactionFeeCollector);
    }

    @Override
    public void process(Entry entry) {
        for (Collector collector : collectors) {
            collector.process(entry);
        }
    }

    public Stats calculateStats(LocalDate endingDate, String targetCurrency) {
        var cashStats = new CashStats(depositCollector.balance(),
                withdrawalsCollector.balance(),
                interestCollector.balance(),
                totalFeesCalculator.balance(),
                dividendCollector.balance(),
                realizedGainsCollector.balance(),
                cashCalculator.balance()
        );

        var valueStats = new ValueStatsDto(
                unrealizedGainsCalculator.calculate(endingDate, targetCurrency),
                accountGainCalculator.calculate(endingDate, targetCurrency),
                holdingsValueCalculator.calculate(endingDate, targetCurrency),
                accountValueCalculator.calculate(endingDate, targetCurrency),
                xirrCalculator.calculate(endingDate, targetCurrency));

        return new Stats(cashStats, valueStats);
    }

    public AccountMetadata getMetadata() {
        var firstActivity = accountOpenDatesCollector.getFirstActivity();
        return new AccountMetadata(
                firstActivity.get(), //TODO add last activity to catch unclosed accounts
                accountOpenDatesCollector.getClosingDate()
        );
    }
}
