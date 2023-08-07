package beanvest.processor.processing.calculator;

import beanvest.journal.CashFlow;
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

public class CashflowsXirrCalculator {
    public Result<BigDecimal, UserErrors> calculateXirr(LocalDate endDate, List<CashFlow> relevantCashFlows, BigDecimal endingValue) {
        var xirrTransactions = convertToXirrTransactions(
                relevantCashFlows, endingValue,
                endDate);

        return xirrTransactions.size() >= 2
                ? calculateStats(xirrTransactions)
                : Result.failure(ErrorFactory.xirrNoTransactions());
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

    private Result<BigDecimal, UserErrors> calculateStats(List<Transaction> transactions) {
        try {
            return Result.success(BigDecimal.valueOf(Xirr.builder().withGuess(10).withTransactions(transactions).xirr()));
        } catch (NonconvergenceException | OverflowException e) {
            return Result.failure(ErrorFactory.xirrCalculationsFailed());
        }
    }
}
