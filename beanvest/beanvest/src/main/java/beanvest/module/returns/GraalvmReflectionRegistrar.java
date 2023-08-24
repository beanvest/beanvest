package beanvest.module.returns;

import beanvest.options.OptionsCliCommand;
import beanvest.processor.deprecated.dto.AccountPeriodDto;
import beanvest.processor.deprecated.dto.PortfolioStatsDto;
import beanvest.processor.deprecated.dto.StatsWithDeltasDto;
import beanvest.processor.deprecated.dto.ValueStatDto;
import beanvest.processor.dto.AccountDetailsDto;
import beanvest.processor.dto.AccountDto2;
import beanvest.processor.dto.EntityType;
import beanvest.processor.time.Period;
import beanvest.processor.time.PeriodInterval;
import beanvest.result.StatErrorEnum;
import beanvest.result.Result;
import beanvest.result.StatError;
import beanvest.result.StatErrors;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeReflection;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public class GraalvmReflectionRegistrar implements Feature {
    @Override
    public void beforeAnalysis(BeforeAnalysisAccess access) {
        //TODO trim it down
        var classes = List.of(
                PortfolioStatsDto.class,
                AccountDetailsDto.class,
                EntityType.class,
                AccountDto2.class,
                AccountPeriodDto.class,
                StatsWithDeltasDto.class,
                ValueStatDto.class,
                Result.class,
                StatError.class,
                StatErrors.class,
                StatErrorEnum.class,
                Optional.class,
                Period.class,
                PeriodInterval.class,

                OptionsCliCommand.OptionsDto.class,
                OptionsCliCommand.ColumnDto.class

        );
        classes.forEach(c -> {
            RuntimeReflection.register(c);
            Arrays.stream(c.getDeclaredFields()).forEach(f -> registerField(f, c));
        });

    }

    private static void registerField(Field f, Class<?> portfolioStatsDtoClass) {
        try {
            RuntimeReflection.register(portfolioStatsDtoClass.getDeclaredField(f.getName()));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}