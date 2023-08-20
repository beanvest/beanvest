package beanvest.module.returns.cli;

import beanvest.processor.StatDefinition;
import beanvest.processor.CollectionMode;
import beanvest.processor.JournalNotFoundException;
import beanvest.processor.dto.PortfolioStatsDto2;
import beanvest.processor.processingv2.validator.ValidatorError;

import java.util.List;

public interface CliOutputWriter {
    void outputResult(List<StatDefinition> selectedColumns, PortfolioStatsDto2 portfolioStats, CollectionMode collectionMode);

    void outputInputErrors(List<ValidatorError> errors);

    void outputException(JournalNotFoundException e);
}
