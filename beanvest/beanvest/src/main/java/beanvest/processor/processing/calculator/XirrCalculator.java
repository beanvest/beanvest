package beanvest.processor.processing.calculator;

import beanvest.result.Result;
import beanvest.journal.CashFlow;
import beanvest.result.ErrorFactory;
import beanvest.result.UserErrors;
import beanvest.processor.processing.collector.FullCashFlowCollector;
import org.decampo.xirr.NonconvergenceException;
import org.decampo.xirr.OverflowException;
import org.decampo.xirr.Transaction;
import org.decampo.xirr.Xirr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class XirrCalculator {
    private static final Logger LOGGER = LoggerFactory.getLogger(XirrCalculator.class.getName());
    public static final String TARGET_CURRENCY = "GBP";
    private final FullCashFlowCollector fullCashFlowCollector;
    private final TotalValueCalculator totalValueCalculator;

    public XirrCalculator(FullCashFlowCollector fullCashFlowCollector, TotalValueCalculator totalValueCalculator) {
        this.fullCashFlowCollector = fullCashFlowCollector;
        this.totalValueCalculator = totalValueCalculator;
    }

    public Result<BigDecimal, UserErrors> xirr(final LocalDate endDate) {
        var totalValueResult = totalValueCalculator.calculateValue(endDate, TARGET_CURRENCY);
        if (totalValueResult.hasError()) {
            return totalValueResult;
        }
        var xirrTransactions = convertToXirrTransactions(
                fullCashFlowCollector.get(), totalValueResult.getValue(),
                endDate);

        return xirrTransactions.size() >= 2
                ? calculateStats(xirrTransactions)
                : Result.failure(ErrorFactory.xirrNoTransactions());
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