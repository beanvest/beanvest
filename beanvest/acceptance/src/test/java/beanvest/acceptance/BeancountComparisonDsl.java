package beanvest.acceptance;

import beanvest.lib.apprunner.AppRunner;
import beanvest.lib.testing.TestFiles;
import beanvest.lib.util.gson.GsonFactory;
import beanvest.processor.dto.PortfolioStatsDto;

import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BeancountComparisonDsl {
    private final AppRunner exportRunner;
    private final AppRunner returnsRunner;
    private BeanReport.Holdings bcHoldings;
    private PortfolioStatsDto bvStats;

    public BeancountComparisonDsl(AppRunner exportRunner, AppRunner returnsRunner) {

        this.exportRunner = exportRunner;
        this.returnsRunner = returnsRunner;
    }

    public void runReports(String journal) {
        try {
            var path = TestFiles.writeToTempFile(journal);

            var returnsResult = returnsRunner.runSuccessfully(List.of(path.toString(), "--json"));
            var cliOutput = returnsRunner.runSuccessfully(List.of(path.toString()));
            bvStats = GsonFactory.createWithProjectDefaults().fromJson(returnsResult.stdOut(), PortfolioStatsDto.class);

            var exportResult = exportRunner.runSuccessfully(List.of(path.toString()));
            var bcJournalFile = TestFiles.writeToTempFile(exportResult.stdOut() + """
                    1970-01-01 open Equity:Bank
                    1970-01-01 open Income:Interest
                    """);
            var beanReport = new BeanReport();
            bcHoldings = beanReport.readHoldings(bcJournalFile);
        } catch (IOException | InterruptedException | CsvException e) {
            throw new RuntimeException(e);
        }
    }

    public void verifyCashMatchesInBeancountAndBeanvest() {
        var bvResult = bvStats.accountDtos.get(0).periodStats.get("TOTAL").cash().stat().value();
        var bcResult = bcHoldings.get("Assets:Savings:Cash", "GBP").units();
        assertThat(bcResult)
                .as("beancount shows GBP holding of `" + bcResult + "` while in beanvest it's `" + bvResult + "`")
                .isEqualByComparingTo(bvResult);
    }
}
