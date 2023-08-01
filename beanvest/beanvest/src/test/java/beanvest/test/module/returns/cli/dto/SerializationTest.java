package beanvest.test.module.returns.cli.dto;

import beanvest.module.returns.CliJsonOutputWriter;
import beanvest.processor.dto.AccountDto;
import beanvest.processor.dto.PortfolioStatsDto;
import beanvest.processor.dto.StatsWithDeltasDto;
import beanvest.processor.dto.ValueStatDto;
import beanvest.processor.processing.PeriodSpec;
import beanvest.processor.time.Period;
import beanvest.processor.time.PeriodInterval;
import beanvest.result.ErrorFactory;
import beanvest.result.Result;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class SerializationTest {
    public static final ValueStatDto VALUE_STAT_DTO = new ValueStatDto(Result.success(BigDecimal.ONE), Optional.of(BigDecimal.ZERO));

    @Test
    void shouldSerializeDtos() {
        var portfolioStatsDto = new PortfolioStatsDto(List.of("trading"),
                List.of(Period.createPeriodCoveringDate(LocalDate.parse("2021-01-01"), new PeriodSpec(LocalDate.MIN, LocalDate.parse("2022-01-01"), PeriodInterval.YEAR)))
                , List.of(new AccountDto("trading", LocalDate.parse("2021-02-03"), Optional.empty(), Map.of(
                "2021", new StatsWithDeltasDto(VALUE_STAT_DTO,
                        VALUE_STAT_DTO,
                        VALUE_STAT_DTO,
                        VALUE_STAT_DTO,
                        VALUE_STAT_DTO,
                        VALUE_STAT_DTO,
                        VALUE_STAT_DTO,
                        VALUE_STAT_DTO,
                        new ValueStatDto(Result.failure(ErrorFactory.accountNotOpenYet()), Optional.of(BigDecimal.ZERO)),
                        VALUE_STAT_DTO,
                        VALUE_STAT_DTO,
                        VALUE_STAT_DTO,
                        VALUE_STAT_DTO,
                        List.of("Validation error"))
        ))));

        var gson = CliJsonOutputWriter.GSON;
        var json = gson.toJson(portfolioStatsDto);
        var backToObj = gson.fromJson(json, PortfolioStatsDto.class);
        assertThat(backToObj).usingRecursiveComparison().isEqualTo(portfolioStatsDto);
    }
}
