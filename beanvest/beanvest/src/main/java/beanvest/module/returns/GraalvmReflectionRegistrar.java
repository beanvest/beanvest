package beanvest.module.returns;

import beanvest.options.OptionsCliCommand;
import beanvest.processor.dto.AccountDto;
import beanvest.processor.dto.AccountPeriodDto;
import beanvest.processor.dto.PortfolioStatsDto;
import beanvest.processor.dto.StatsWithDeltasDto;
import beanvest.processor.dto.ValueStatDto;
import beanvest.processor.processingv2.dto.AccountDto2;
import beanvest.processor.time.Period;
import beanvest.processor.time.PeriodInterval;
import beanvest.result.ErrorEnum;
import beanvest.result.Result;
import beanvest.result.UserError;
import beanvest.result.UserErrors;
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
                AccountDto2.class,
                AccountPeriodDto.class,
                StatsWithDeltasDto.class,
                ValueStatDto.class,
                Result.class,
                UserError.class,
                UserErrors.class,
                ErrorEnum.class,
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