package beanvest.returns;

import beanvest.tradingjournal.JournalNotFoundException;
import beanvest.tradingjournal.PortfolioStats;
import beanvest.tradingjournal.model.UserError;

import java.util.List;

public interface CliOutputWriter {
    void outputResult(List<String> selectedColumns, PortfolioStats portfolioStats);

    void outputInputErrors(List<UserError> errors);

    void outputException(JournalNotFoundException e);
}
