package beanvest.returns;

import beanvest.test.tradingjournal.JournalNotFoundException;
import beanvest.test.tradingjournal.PortfolioStats;
import beanvest.test.tradingjournal.model.UserError;

import java.util.List;

public interface CliOutputWriter {
    void outputResult(List<String> selectedColumns, PortfolioStats portfolioStats);

    void outputInputErrors(List<UserError> errors);

    void outputException(JournalNotFoundException e);
}
