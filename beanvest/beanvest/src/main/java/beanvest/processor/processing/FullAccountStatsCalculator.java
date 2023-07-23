package beanvest.processor.processing;

import beanvest.processor.ValueStatsDto;
import beanvest.journal.entry.Entry;
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
import beanvest.processor.processing.collector.CashCalculator;
import beanvest.processor.processing.collector.FullCashFlowCollector;
import beanvest.processor.processing.collector.HoldingsCollector;
import beanvest.processor.processing.collector.InterestCollector;
import beanvest.processor.processing.collector.RealizedGainsCollector;
import beanvest.processor.processing.collector.SimpleFeeCollector;
import beanvest.processor.processing.collector.SpentCollector;
import beanvest.processor.processing.collector.WithdrawalCollector;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class FullAccountStatsCalculator implements Collector {
    private final DepositCollector depositCollector = new DepositCollector();
    private final WithdrawalCollector withdrawalsCollector = new WithdrawalCollector();
    private final SimpleFeeCollector simpleFeeCollector = new SimpleFeeCollector();
    private final TransactionFeeCollector transactionFeeCollector = new TransactionFeeCollector();
    private final InterestCollector interestCollector = new InterestCollector();
    private final DividendCollector dividendCollector = new DividendCollector();
    private final EarnedCollector earnedCollector = new EarnedCollector();
    private final SpentCollector spentCollector = new SpentCollector();
    private final HoldingsCollector holdingsCollector = new HoldingsCollector();
    private final RealizedGainsCollector realizedGainsCollector = new RealizedGainsCollector(holdingsCollector);
    private final AccountOpenDatesCollector accountOpenDatesCollector = new AccountOpenDatesCollector();
    private final FullCashFlowCollector fullCashFlowCollector = new FullCashFlowCollector();
    private final HoldingsValueCalculator holdingsValueCalculator;
    private final XirrCalculator xirrCalculator;
    private final CashCalculator cashCalculator = new CashCalculator(
            depositCollector, withdrawalsCollector, interestCollector,
            simpleFeeCollector, dividendCollector, spentCollector, earnedCollector);
    private final HoldingsCostCalculator holdingsCostCalculator = new HoldingsCostCalculator(holdingsCollector);
    private final List<Collector> collectors = List.of(
            holdingsCollector,
            realizedGainsCollector,
            depositCollector,
            withdrawalsCollector,
            simpleFeeCollector,
            transactionFeeCollector,
            interestCollector,
            dividendCollector,
            earnedCollector,
            spentCollector,
            accountOpenDatesCollector,
            fullCashFlowCollector
    );
    private final TotalValueCalculator totalValueCalculator;
    private final UnrealizedGainsCalculator unrealizedGainsCalculator;

    public FullAccountStatsCalculator(LatestPricesBook pricesBook) {
        holdingsValueCalculator = new HoldingsValueCalculator(holdingsCollector, pricesBook);
        unrealizedGainsCalculator = new UnrealizedGainsCalculator(holdingsValueCalculator, holdingsCostCalculator);
        totalValueCalculator = new TotalValueCalculator(holdingsValueCalculator, cashCalculator);
        xirrCalculator = new XirrCalculator(fullCashFlowCollector, totalValueCalculator);
    }

    @Override
    public void process(Entry entry) {
        for (Collector collector : collectors) {
            collector.process(entry);
        }
    }

    public Stats calculateStats(LocalDate endingDate, String targetCurrency) {
        CashStats cashStats = new CashStats(depositCollector.balance(),
                withdrawalsCollector.balance(),
                interestCollector.balance(),
                simpleFeeCollector.balance().add(transactionFeeCollector.balance()),
                dividendCollector.balance(),
                realizedGainsCollector.balance(),
                cashCalculator.balance()
        );

        var holdingsValueResult = holdingsValueCalculator.calculateValue(endingDate, targetCurrency);
        Optional<ValueStatsDto> maybeValueStats;
        if (holdingsValueResult.hasError()) {
            maybeValueStats = Optional.empty();
        } else {
            var holdingsValue = holdingsValueResult.getValue();
            var unrealizedGains = unrealizedGainsCalculator.calculate(endingDate, targetCurrency).getValue();
            var accountGain = holdingsValue.amount()
                    .add(cashStats.cash())
                    .subtract(cashStats.deposits())
                    .subtract(cashStats.withdrawals());
            maybeValueStats = Optional.of(new ValueStatsDto(
                    unrealizedGains,
                    accountGain,
                    holdingsValueResult.getValue().amount(),
                    holdingsValueResult.getValue().amount().add(cashCalculator.balance()),
                    xirrCalculator.xirr(endingDate)));
        }

        return new Stats(cashStats, maybeValueStats);
    }

    public AccountMetadata getMetadata() {
        var firstActivity = accountOpenDatesCollector.getFirstActivity();
        return new AccountMetadata(
                firstActivity.get(), //TODO add last activity to catch unclosed accounts
                accountOpenDatesCollector.getClosingDate()
        );
    }
}
