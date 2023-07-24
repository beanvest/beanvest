package beanvest.processor.processing.calculator;

import beanvest.processor.processing.collector.SimpleFeeCollector;
import beanvest.processor.processing.collector.TransactionFeeCollector;

import java.math.BigDecimal;

public class TotalFeesCalculator {

    private final SimpleFeeCollector simpleFeeCollector;
    private final TransactionFeeCollector transactionFeeCollector;

    public TotalFeesCalculator(SimpleFeeCollector simpleFeeCollector, TransactionFeeCollector transactionFeeCollector) {
        this.simpleFeeCollector = simpleFeeCollector;
        this.transactionFeeCollector = transactionFeeCollector;
    }

    public BigDecimal balance() {
        return transactionFeeCollector.balance().add(simpleFeeCollector.balance());
    }
}
