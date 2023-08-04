package beanvest.acceptance.returns.processingrework;

import beanvest.result.ErrorFactory;
import beanvest.result.Result;
import beanvest.result.UserErrors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class SubtractingDeltaCalculator implements Calculator {
    private final Calculator calculator;

    private LocalDate endDate;
    private Map<String, Result<BigDecimal, UserErrors>> current = new HashMap<>();
    private Map<String, Result<BigDecimal, UserErrors>> previous = new HashMap<>();

    public SubtractingDeltaCalculator(Calculator calculator) {
        this.calculator = calculator;
    }

    @Override
    public final Result<BigDecimal, UserErrors> calculate(String account, LocalDate endDate, String targetCurrency) {
        if (!endDate.equals(this.endDate)) {
            previous = current;
            current = new HashMap<>();
            this.endDate = endDate;
        }
        var c = calculator.calculate(account, endDate, targetCurrency);
        current.put(account, c);
        var p = previous.get(account);
        if (p == null) {
            p = Result.success(BigDecimal.ZERO);
        }
        return c.combine(p, BigDecimal::subtract, UserErrors::join);
    }
}
