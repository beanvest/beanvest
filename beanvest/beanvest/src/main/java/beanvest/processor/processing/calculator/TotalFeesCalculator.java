package beanvest.processor.processing.calculator;

import beanvest.processor.processing.collector.SimpleFeeCollector;
import beanvest.processor.processing.collector.TransactionFeeCollector;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.util.List;

public class TotalFeesCalculator {

    private final SimpleFeeCollector simpleFeeCollector;
    private final TransactionFeeCollector transactionFeeCollector;

    public TotalFeesCalculator(SimpleFeeCollector simpleFeeCollector, TransactionFeeCollector transactionFeeCollector) {
        this.simpleFeeCollector = simpleFeeCollector;
        this.transactionFeeCollector = transactionFeeCollector;
    }

    public Result<BigDecimal, UserErrors> balance() {
        var simpleFees = simpleFeeCollector.balance();
        var transactionFees = transactionFeeCollector.balance();
        return Result.combine(List.of(simpleFees, transactionFees),
                BigDecimal::add,
                UserErrors::join
        );
    }
}
