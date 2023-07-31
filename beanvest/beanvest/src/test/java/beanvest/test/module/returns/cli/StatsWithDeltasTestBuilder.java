package beanvest.test.module.returns.cli;

import beanvest.result.Result;
import beanvest.processor.dto.StatsWithDeltasDto;
import beanvest.processor.dto.ValueStatDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class StatsWithDeltasTestBuilder {
    private final LocalDate periodEnd = LocalDate.parse("2020-01-01");
    private final BigDecimal deposits = new BigDecimal(12300);
    private final BigDecimal withdrawals = new BigDecimal("3322");
    private BigDecimal interest = new BigDecimal("12.3");
    private BigDecimal fees = new BigDecimal("10.44");
    private BigDecimal dividends = new BigDecimal("10");
    private BigDecimal realizedGains = new BigDecimal("822.3");
    private BigDecimal cash = new BigDecimal("77.32");
    private BigDecimal holdingsValue = new BigDecimal(11192);
    private final BigDecimal accountValue = new BigDecimal(11193);
    private BigDecimal unrealizedGain = new BigDecimal("131.94");
    private BigDecimal xirr = new BigDecimal("8.3");
    private BigDecimal accountGain = new BigDecimal("3.2");

    public static StatsWithDeltasTestBuilder builder() {
        return new StatsWithDeltasTestBuilder();
    }


    public StatsWithDeltasTestBuilder setInterest(String interest) {
        this.interest = new BigDecimal(interest);
        return this;
    }

    public StatsWithDeltasTestBuilder setFees(String fees) {
        this.fees = new BigDecimal(fees);
        return this;
    }

    public StatsWithDeltasTestBuilder setDividends(String dividends) {
        this.dividends = new BigDecimal(dividends);
        return this;
    }

    public StatsWithDeltasTestBuilder setRealizedGains(String realizedGains) {
        this.realizedGains = new BigDecimal(realizedGains);
        return this;
    }

    public StatsWithDeltasTestBuilder setCash(String cash) {
        this.cash = new BigDecimal(cash);
        return this;
    }

    public StatsWithDeltasTestBuilder setHoldingsValue(String holdingsValue) {
        this.holdingsValue = new BigDecimal(holdingsValue);
        return this;
    }

    public StatsWithDeltasTestBuilder setAccountGain(String accountGain) {
        this.accountGain = new BigDecimal(accountGain);
        return this;
    }

    public StatsWithDeltasTestBuilder setUnrealizedGain(String unrealizedGain) {
        this.unrealizedGain = new BigDecimal(unrealizedGain);
        return this;
    }

    public StatsWithDeltasTestBuilder setXirr(String xirr) {
        this.xirr = new BigDecimal(xirr);
        return this;
    }

    public StatsWithDeltasDto build() {
        return new StatsWithDeltasDto(
                new ValueStatDto(Result.success(deposits), Optional.empty()),
                new ValueStatDto(Result.success(withdrawals), Optional.empty()),
                new ValueStatDto(Result.success(interest), Optional.empty()),
                new ValueStatDto(Result.success(fees), Optional.empty()),
                new ValueStatDto(Result.success(dividends), Optional.empty()),
                new ValueStatDto(Result.success(realizedGains), Optional.empty()),
                new ValueStatDto(Result.success(cash), Optional.empty()),
                new ValueStatDto(Result.success(unrealizedGain), Optional.empty()),
                new ValueStatDto(Result.success(accountGain), Optional.empty()),
                new ValueStatDto(Result.success(holdingsValue), Optional.empty()),
                new ValueStatDto(Result.success(accountValue), Optional.empty()),
                new ValueStatDto(Result.success(xirr), Optional.empty()),
                List.of());
    }
}
