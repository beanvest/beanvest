package beanvest.processor.processingv2.processor;

import beanvest.journal.entry.AccountOperation;
import beanvest.journal.entry.Sell;
import beanvest.processor.processingv2.Calculator;
import beanvest.processor.processingv2.Processor;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RealizedGainCalculator implements Processor, Calculator {
    SimpleBalanceCollector simpleBalanceCollector = new SimpleBalanceCollector();
    private HoldingsCollector holdingsCollector;

    public RealizedGainCalculator() {
        this.holdingsCollector = new HoldingsCollector();
    }

    @Override
    public void process(AccountOperation op) {
        holdingsCollector.process(op);
        if (op instanceof Sell sell) {
            var unitCost = holdingsCollector.getHolding(op.account() + ":" + sell.holdingSymbol()).averageCost();
            var totalCost = unitCost.multiply(sell.units());
            var realizedGain = sell.totalPrice().amount().subtract(totalCost);
            simpleBalanceCollector.add(sell.account() + ":" + sell.holdingSymbol(), realizedGain);
        }
    }

    @Override
    public Result<BigDecimal, UserErrors> calculate(String account, LocalDate endDate, String targetCurrency) {
        return simpleBalanceCollector.calculate(account);
    }
}
