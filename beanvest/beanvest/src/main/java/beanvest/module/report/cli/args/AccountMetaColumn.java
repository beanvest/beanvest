package beanvest.module.report.cli.args;

import beanvest.processor.dto.AccountDto2;

import java.util.function.Function;

public record AccountMetaColumn(String shortName, String description, Function<AccountDto2, String> extractor) implements CliColumn {
}
