package beanvest.module.returns;

import beanvest.processor.JournalNotFoundException;
import beanvest.processor.PortfolioStatsDto;
import beanvest.result.UserError;

import java.util.List;

public interface CliOutputWriter {
    void outputResult(List<String> selectedColumns, PortfolioStatsDto portfolioStats);

    void outputInputErrors(List<UserError> errors);

    void outputException(JournalNotFoundException e);
}
