package beanvest.processor.processingv2.processor;

import beanvest.processor.processingv2.CalculationParams;
import beanvest.processor.processingv2.Calculator;
import beanvest.result.Result;
import beanvest.result.StatErrors;

import java.math.BigDecimal;
import java.util.List;

public class AccountGainCalculator implements Calculator {
    private final UnrealizedGainCalculator unrealizedGainCalculator;
    private final InterestCalculator interestCalculator;
    private final DividendCalculator dividendCalculator;
    private final RealizedGainCalculator realizedGainCalculator;
    private final PlatformFeeCalculator platformFeeCalculator;

    public AccountGainCalculator(
            UnrealizedGainCalculator unrealizedGainCalculator,
            InterestCalculator interestCalculator,
            DividendCalculator dividendCalculator,
            RealizedGainCalculator realizedGainCalculator,
            PlatformFeeCalculator platformFeeCalculator
    ) {

        this.unrealizedGainCalculator = unrealizedGainCalculator;
        this.interestCalculator = interestCalculator;
        this.dividendCalculator = dividendCalculator;
        this.realizedGainCalculator = realizedGainCalculator;
        this.platformFeeCalculator = platformFeeCalculator;
    }

    @Override
    public Result<BigDecimal, StatErrors> calculate(CalculationParams params) {
        return Result.combine(List.of(
                unrealizedGainCalculator.calculate(params),
                interestCalculator.calculate(params),
                dividendCalculator.calculate(params),
                realizedGainCalculator.calculate(params),
                platformFeeCalculator.calculate(params)
        ), BigDecimal::add, StatErrors::join);
    }
}
