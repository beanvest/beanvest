package beanvest.module.returns.cli.args;


import beanvest.processor.processingv2.Grouping;

import java.util.Arrays;

import static beanvest.processor.processingv2.Grouping.*;


public enum AccountGroupingCliArg {
    yes(WITH_GROUPS),
    no(NO_GROUPS),
    only(ONLY_GROUPS);

    public static AccountGroupingCliArg DEFAULT = yes;

    public final Grouping mappedValue;

    AccountGroupingCliArg(Grouping grouping) {
        mappedValue = grouping;
    }

    public static String[] valuesAsStrings()
    {
        return Arrays.stream(values()).map(Enum::toString).toArray(String[]::new);
    }
}
