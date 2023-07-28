package beanvest.module.returns;

import beanvest.processor.dto.PortfolioStatsDto;

import java.util.ArrayList;
import java.util.List;

public class ErrorMessagesExtractor {
    List<String> extractErrorsMessages(PortfolioStatsDto periodStats) {
        var result = new ArrayList<String>();
        for (var accountDto : periodStats.accountDtos) {
            for (var period : periodStats.periods) {
                var statsWithDeltasDto = accountDto.periodStats.get(period.title());
                if (statsWithDeltasDto != null) {
                    result.addAll(statsWithDeltasDto.errors());
                }
            }
        }
        return result;
    }
}
