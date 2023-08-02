package beanvest.processor.processing.calculator;

import beanvest.journal.CashFlow;
import beanvest.journal.Value;
import beanvest.processor.processing.AccountType;
import beanvest.processor.processing.collector.PeriodCashFlowCollector;
import beanvest.result.ErrorFactory;
import beanvest.result.Result;
import beanvest.result.UserErrors;
import org.decampo.xirr.NonconvergenceException;
import org.decampo.xirr.OverflowException;
import org.decampo.xirr.Transaction;
import org.decampo.xirr.Xirr;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class XirrPeriodicCalculator {
    private final PeriodCashFlowCollector fullCashFlowCollector;
    private final TotalValueCalculator totalValueCalculator;
    private final HoldingsValueCalculator holdingsValueCalculator;
    private final AccountType accountType;
    private Result<BigDecimal, UserErrors> previousValue = Result.success(BigDecimal.ZERO);
    private LocalDate previousDate = LocalDate.MIN;

    public XirrPeriodicCalculator(PeriodCashFlowCollector fullCashFlowCollector, TotalValueCalculator totalValueCalculator, HoldingsValueCalculator holdingsValueCalculator, AccountType accountType) {
        this.fullCashFlowCollector = fullCashFlowCollector;
        this.totalValueCalculator = totalValueCalculator;
        this.holdingsValueCalculator = holdingsValueCalculator;
        this.accountType = accountType;
    }

    public Result<BigDecimal, UserErrors> calculate(final LocalDate endDate, String targetCurrency) {
        var totalValueResult = calculateEndingValue(endDate, targetCurrency);
        if (totalValueResult.hasError()) {
            return totalValueResult;
        }

        var relevantCashFlows = fullCashFlowCollector.get();
        relevantCashFlows.add(0, new CashFlow(previousDate, Value.of(previousValue.getValue(), "GBP")));
        var xirrTransactions = convertToXirrTransactions(
                relevantCashFlows, totalValueResult.getValue(),
                endDate);

        var result = xirrTransactions.size() >= 2
                ? calculateStats(xirrTransactions)
                : Result.<BigDecimal, UserErrors>failure(ErrorFactory.xirrNoTransactions());

        previousValue = totalValueResult;
        previousDate = endDate;
        return result;
    }

    private Result<BigDecimal, UserErrors> calculateEndingValue(LocalDate endDate, String targetCurrency) {
        if (accountType == AccountType.HOLDING) {
            return holdingsValueCalculator.calculate(endDate, targetCurrency);
        } else {
            return totalValueCalculator.calculateValue(endDate, targetCurrency);
        }
    }

    private Result<BigDecimal, UserErrors> calculateStats(List<Transaction> transactions) {
        try {
            return Result.success(BigDecimal.valueOf(Xirr.builder().withGuess(10).withTransactions(transactions).xirr()));
        } catch (NonconvergenceException | OverflowException e) {
            return Result.failure(ErrorFactory.xirrCalculationsFailed());
        }
    }

    private List<Transaction> convertToXirrTransactions(List<CashFlow> relevantCashFlows, BigDecimal endingAccountValue, LocalDate endDate) {
        var transactionsFromJournal = relevantCashFlows.stream()
                .filter(cf -> cf.transferredAmount().amount().abs().compareTo(BigDecimal.ZERO) != 0)
                .map(cf -> new Transaction(cf.transferredAmount().negate().amount().doubleValue(), cf.date())).toList();
        var xirrTransactions = new ArrayList<>(transactionsFromJournal);
        if (endingAccountValue.compareTo(BigDecimal.ZERO) != 0) {
            xirrTransactions.add(new Transaction(endingAccountValue.doubleValue(), endDate));
        }
        return mergeTransactionsOnTheSameDay(xirrTransactions).collect(toList());
    }

    private Stream<Transaction> mergeTransactionsOnTheSameDay(List<Transaction> xirrTransactions) {
        return xirrTransactions.stream()
                .collect(Collectors.toMap(Transaction::getWhen, Transaction::getAmount, Double::sum))
                .entrySet()
                .stream()
                .map(e -> new Transaction(e.getValue(), e.getKey()));
    }
}