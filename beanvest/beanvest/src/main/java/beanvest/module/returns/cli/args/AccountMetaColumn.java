package beanvest.module.returns.cli.args;

import beanvest.processor.processingv2.dto.AccountDto2;

import java.util.function.Function;

public record AccountMetaColumn(String shortName, String description, Function<AccountDto2, String> extractor) implements CliColumn {
}
