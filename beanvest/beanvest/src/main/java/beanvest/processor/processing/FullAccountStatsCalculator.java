package beanvest.processor.processing;

import beanvest.journal.CashStats;
import beanvest.journal.Stats;
import beanvest.journal.entry.Entry;
import beanvest.processor.dto.ValueStatsDto;
import beanvest.processor.pricebook.LatestPricesBook;
import beanvest.processor.processing.calculator.AccountGainCalculator;
import beanvest.processor.processing.calculator.AccountValueCalculator;
import beanvest.processor.processing.calculator.Calculator;
import beanvest.processor.processing.calculator.CashCalculator;
import beanvest.processor.processing.calculator.HoldingsCostCalculator;
import beanvest.processor.processing.calculator.HoldingsValueCalculator;
import beanvest.processor.processing.calculator.TotalFeesCalculator;
import beanvest.processor.processing.calculator.TotalValueCalculator;
import beanvest.processor.processing.calculator.UnrealizedGainsCalculator;
import beanvest.processor.processing.calculator.XirrCalculator;
import beanvest.processor.processing.collector.AccountOpenDatesCollector;
import beanvest.processor.processing.collector.DepositCollector;
import beanvest.processor.processing.collector.DividendCollector;
import beanvest.processor.processing.collector.EarnedCollector;
import beanvest.processor.processing.collector.FullCashFlowCollector;
import beanvest.processor.processing.collector.HoldingsCollector;
import beanvest.processor.processing.collector.InterestCollector;
import beanvest.processor.processing.collector.RealizedGainsCollector;
import beanvest.processor.processing.collector.SimpleFeeCollector;
import beanvest.processor.processing.collector.SpentCollector;
import beanvest.processor.processing.collector.TransactionFeeCollector;
import beanvest.processor.processing.collector.WithdrawalCollector;
import beanvest.processor.processing.validator.BalanceValidator;
import beanvest.processor.processing.validator.CloseValidator;
import beanvest.processor.processing.validator.Validator;
import beanvest.processor.validation.ValidatorError;
import beanvest.result.ErrorFactory;
import beanvest.result.Result;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class FullAccountStatsCalculator {
    public static final Calculator DISABLED_CALCULATOR = () -> Result.failure(ErrorFactory.disabled());
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

    private final LinkedHashSet<ValidatorError> validationErrors = new LinkedHashSet<>();
    private final BalanceValidator balanceValidator = new BalanceValidator(validationErrors::add,
            new CashCalculator(depositCollector, withdrawalsCollector, interestCollector, simpleFeeCollector,
                    dividendCollector, spentCollector, earnedCollector));
    private final CloseValidator closeValidator = new CloseValidator(validationErrors::add, new CashCalculator(depositCollector, withdrawalsCollector, interestCollector, simpleFeeCollector,
            dividendCollector, spentCollector, earnedCollector));
    private final List<Processor> collectors = List.of(
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
            withdrawalsCollector);
    private final List<Validator> validators = List.of(
            balanceValidator,
            closeValidator);
    private final List<Processor> processors = new ArrayList<>();
    private final AccountType accountType;

    public FullAccountStatsCalculator(LatestPricesBook pricesBook, AccountType accountType) {

        this.accountType = accountType;
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

        this.processors.addAll(collectors);
        if (accountType == AccountType.ACCOUNT) {
            this.processors.addAll(validators);
        }
    }

    public LinkedHashSet<ValidatorError> process(Entry entry) {
        for (Processor collector : processors) {
            collector.process(entry);
        }

        return validationErrors;
    }

    public Stats calculateStats(LocalDate endingDate, String targetCurrency) {
        var cashStats = new CashStats(depositCollector.balance(),
                withdrawalsCollector.balance(),
                interestCollector.balance(),
                totalFeesCalculator.balance(),
                dividendCollector.balance(),
                realizedGainsCollector.balance(),
                accountType == AccountType.HOLDING ? Result.failure(ErrorFactory.disabledForAccountType()) : cashCalculator.calculate()
        );
        var valueStats = new ValueStatsDto(
                unrealizedGainsCalculator.calculate(endingDate, targetCurrency),
                accountGainCalculator.calculate(endingDate, targetCurrency),
                holdingsValueCalculator.calculate(endingDate, targetCurrency),
                accountValueCalculator.calculate(endingDate, targetCurrency),
                xirrCalculator.calculate(endingDate, targetCurrency));

        var errorMessages = valueStats.getErrorMessages();
        return new Stats(cashStats, valueStats, errorMessages);
    }

    public AccountMetadata getMetadata() {
        var firstActivity = accountOpenDatesCollector.getFirstActivity();
        return new AccountMetadata(
                firstActivity.get(), //TODO add last activity to catch unclosed accounts
                accountOpenDatesCollector.getClosingDate()
        );
    }
}
