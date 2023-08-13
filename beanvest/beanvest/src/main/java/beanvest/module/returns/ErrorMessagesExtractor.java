package beanvest.module.returns;

import beanvest.processor.dto.PortfolioStatsDto;
import beanvest.processor.processingv2.dto.PortfolioStatsDto2;

import java.util.ArrayList;
import java.util.List;

public class ErrorMessagesExtractor {
    List<String> extractErrorsMessages(PortfolioStatsDto2 periodStats) {
        var result = new ArrayList<String>();
        for (var accountDto : periodStats.accountDtos()) {
            for (var period : periodStats.periods()) {
                var statsWithDeltasDto = accountDto.periodStats().get(period);
                if (statsWithDeltasDto != null) {
                    result.addAll(statsWithDeltasDto.errors());
                }
            }
        }
        return result;
    }
}
