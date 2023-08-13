package beanvest.module.returns;

import beanvest.processor.CollectionMode;
import beanvest.processor.JournalNotFoundException;
import beanvest.processor.processingv2.dto.PortfolioStatsDto2;
import beanvest.processor.validation.ValidatorError;

import java.util.List;

public interface CliOutputWriter {
    void outputResult(List<StatDefinition> selectedColumns, PortfolioStatsDto2 portfolioStats, CollectionMode collectionMode);

    void outputInputErrors(List<ValidatorError> errors);

    void outputException(JournalNotFoundException e);
}
