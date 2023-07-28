package beanvest.module.returns;

import beanvest.processor.CollectionMode;
import beanvest.processor.JournalNotFoundException;
import beanvest.processor.PortfolioStatsDto;
import beanvest.processor.validation.ValidatorError;

import java.util.List;

public interface CliOutputWriter {
    void outputResult(List<String> selectedColumns, PortfolioStatsDto portfolioStats, CollectionMode collectionMode);

    void outputInputErrors(List<ValidatorError> errors);

    void outputException(JournalNotFoundException e);
}
