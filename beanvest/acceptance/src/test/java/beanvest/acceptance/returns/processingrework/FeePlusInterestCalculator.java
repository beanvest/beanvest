package beanvest.acceptance.returns.processingrework;

import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.time.LocalDate;

class FeePlusInterestCalculator implements Calculator {
    private final FeeCollector feeCollector;
    private final InterestCollector interestCollector;

    public FeePlusInterestCalculator(FeeCollector feeCollector, InterestCollector interestCollector) {
        this.feeCollector = feeCollector;
        this.interestCollector = interestCollector;
    }


    @Override
    public Result<BigDecimal, UserErrors> calculate(String account, LocalDate endDate, String targetCurrency) {
        return feeCollector.calculate(account, endDate, targetCurrency)
                .combine(interestCollector.calculate(account, endDate, targetCurrency), BigDecimal::add, UserErrors::join);
    }
}
