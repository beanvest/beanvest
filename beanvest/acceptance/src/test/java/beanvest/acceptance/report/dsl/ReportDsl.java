package beanvest.acceptance.report.dsl;

import beanvest.BeanvestMain;
import beanvest.lib.apprunner.CliExecutionResult;
import beanvest.lib.testing.TestFiles;
import beanvest.lib.testing.asserts.AssertCliExecutionResult;
import beanvest.lib.util.gson.GsonFactory;
import beanvest.processor.deprecated.dto.ValueStatDto;
import beanvest.processor.dto.StatsV2;
import beanvest.processor.dto.AccountDto2;
import beanvest.processor.dto.PortfolioStatsDto2;
import beanvest.result.StatErrorEnum;
import beanvest.result.Result;
import beanvest.result.StatErrors;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.assertj.core.data.Offset;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("UnusedReturnValue")
public class ReportDsl {
    public static final Gson GSON = GsonFactory.builderWithProjectDefaults().create();

    public static final String TOTAL = "TOTAL";
    public static final String DEFAULT_OFFSET = "0.05";
    private final BeanvestRunner beanvestRunner = BeanvestRunner.createRunner(BeanvestMain.class, "report");
    private final ReportOptions reportOptions = new ReportOptions();
    private final Path tempDirectory;
    private final TestFiles testFiles = new TestFiles();

    private CliExecutionResult cliRunResult;

    public ReportDsl() {
        this.tempDirectory = testFiles.createTempDirectory();
    }

    public void verifyOutputIsValidJson() {
        GSON.toJsonTree(cliRunResult.stdOut());
    }

    public void setEnd(String endDate) {
        reportOptions.end = endDate;
    }

    public void setStartDate(String startDate) {
        reportOptions.start = startDate;
    }

    public void calculateReturns(String ledgersContent) {
        final List<String> allLedgers = writeToTempFiles(ledgersContent);
        this.cliRunResult = beanvestRunner.calculateReturns(allLedgers, reportOptions);
    }

    public void calculateReturnsForDirectory(String ledgersDir) {
        this.cliRunResult = beanvestRunner.calculateReturns(List.of(tempDirectory.toString() + "/" + ledgersDir), reportOptions);
    }

    public void calculateReturnsForJournalPaths(String... ledgerPaths) {
        this.cliRunResult = beanvestRunner.calculateReturns(Arrays.stream(ledgerPaths).toList(), reportOptions);
    }

    public ReportDsl verifyStdErrContains(String string) {
        assertThat(cliRunResult.stdErr()).contains(string);
        return this;
    }

    public void setAllowNonZeroExitCodes() {
        reportOptions.allowNonZeroExitCodes = true;
    }

    public void verifyReturnedAnError(String message) {
        var parts = message.strip().split("\\*");

        try {
            Arrays.stream(parts).forEach(this::verifyStdErrContains);
        } catch (AssertionError e) {
            throw new AssertionError(String.format("""
                    Expecting actual:
                      "%s"
                    to match (with wildcards "*"):
                      "%s\"""", this.cliRunResult.stdErr(), message), e);
        }
    }

    public void verifyDidNotPrintStackTrace() {
        assertThat(cliRunResult.stdErr()).doesNotContain("bb.ledger");
    }

    public void setJsonOutput() {
        reportOptions.jsonOutput = true;
    }

    public void verifyClosingDate(String account, String expectedClosingDate) {
        var first = getAccountResults(account);
        assertThat(first.closingDate())
                .isEqualTo(Optional.of(LocalDate.parse(expectedClosingDate)));
    }

    public void verifyZeroExitCode() {
        assertThat(cliRunResult.exitCode()).isEqualTo(0);
    }

    public void setAccountFilter(String regexp) {
        reportOptions.account = regexp;
    }

    public void setYearly() {
        reportOptions.interval = "year";
    }

    public void verifyHasStats(String trading, String period) {
        var accountPeriodReturns = getAccountPeriodReturns(trading, period);
        assertThat(accountPeriodReturns).isNotEmpty();
    }

    public void verifyHasStats(String account) {
        assertThat(getResultDto().accounts().keySet()).contains(account);
    }

    public void verifyHasNoStats(String trading, String period) {
        var accountReturns = getAccountPeriodReturns(trading, period);
        assertThat(accountReturns).isNotPresent();
    }

    public void verifyHasNoStats(String account) {
        assertThat(getResultDto().accounts().keySet()).doesNotContain(account);
    }

    public void setColumns(String... columns) {
        reportOptions.columns = List.of(columns);
    }


    public void verifyStatError(String account, String period, String column, String expectedError) {
        var stat = getStat(account, period, column);
        assertThat(stat.hasError())
                .as(account + "@" + period + " was expected to have no stats")
                .isTrue();
        var statErrors = stat.getErrorAsList().stream().flatMap(e -> e.errors.stream()).map(e -> e.error.toString()).toList();
        assertThat(statErrors).contains(expectedError);
    }

    public void verifyAccountOpeningDate(String account, String openingDate) {
        assertThat(getAccountResults(account).openingDate()).isEqualTo(LocalDate.parse(openingDate));
    }

    public void verifyAccountClosingDate(String account, String closingDate) {
        var expected = Optional.ofNullable(closingDate == null ? null : LocalDate.parse(closingDate));
        assertThat(getAccountResults(account).closingDate())
                .isEqualTo(expected);
    }

    public void verifyResultsReturnedForAccount(String account) {
        assertThat(isAccountInResults(account)).isTrue();
    }

    public void setGroupsOnly() {
        reportOptions.groups = ReportOptions.Groups.ONLY;
    }

    public void setGroupingDisabled() {
        this.reportOptions.groups = ReportOptions.Groups.NO;
    }

    public void verifyFeesTotal(String account, String period, String amount) {
        verifyStat(account, period, amount, ReportFields.CUMULATIVE_FEES);
    }

    public void verifyRealizedGains(String account, String period, String amount) {
        verifyStat(account, period, amount, ReportFields.CUMULATIVE_REALIZED_GAINS);
    }

    public void verifyCurrencyGain(String account, String period, String amount) {
        verifyStat(account, period, amount, ReportFields.CUMULATIVE_CURRENCY_GAINS);
    }

    public void verifyUnrealizedGains(String account, String period, String amount) {
        verifyStat(account, period, amount, ReportFields.CUMULATIVE_UNREALIZED_GAINS);
    }

    public void verifyDividends(String account, String period, String amount) {
        verifyStat(account, period, amount, ReportFields.CUMULATIVE_DIVIDEND);
    }

    public void verifyCash(String account, String period, String amount) {
        verifyStat(account, period, amount, ReportFields.CUMUALTIVE_CASH);
    }

    public void setDeltas() {
        reportOptions.delta = true;
    }

    public void verifyDeposits(String account, String period, String amount) {
        verifyStat(account, period, amount, ReportFields.CUMULATIVE_DEPOSITS);
    }

    public void verifyWithdrawals(String account, String period, String amount) {
        verifyStat(account, period, amount, ReportFields.WITHDRAWALS);
    }

    public void verifyInterest(String account, String period, String amount) {
        this.verifyStat(account, period, amount, ReportFields.CUMULATIVE_INTEREST);
    }

    public void setGroupingEnabled() {
        reportOptions.groups = ReportOptions.Groups.YES;
    }

    public void setQuarterly() {
        reportOptions.interval = "quarter";
    }

    public void setMonthly() {
        reportOptions.interval = "month";
    }

    public void setCurrency(String currency) {
        reportOptions.currency = currency;
    }

    public void setCurrentDate(String s) {
        reportOptions.overrideToday = s;
    }

    private boolean isAccountInResults(String account) {
        return getResultDto().accountDtos().stream()
                .anyMatch(accountReturns -> accountReturns.account().equals(account));
    }

    public void verifyResultsNotReturnedForAccount(String account) {
        assertThat(isAccountInResults(account)).isFalse();
    }


    private List<String> writeToTempFiles(String ledgers) {
        return Stream.of(ledgers)
                .map(ledger -> ledger.replace("$$TODAY$$", LocalDate.now().toString()))
                .flatMap(ledger -> Stream.of(ledger.split("---")))
                .map(content -> TestFiles.writeToTempFile(content).toString())
                .toList();
    }

    public void verifyOutput(String expectedOutput) {
        var processedExpectedOutput = expectedOutput.replace("$$$TODAY$$$", LocalDate.now().toString());
        AssertCliExecutionResult.assertExecution(cliRunResult)
                .ranSuccessfully()
                .outputIs(processedExpectedOutput);
    }

    public ReportDsl verifyAccountGain(String account, String period, String amount) {
        verifyStat(account, period, amount, ReportFields.ACCOUNT_GAIN);
        return this;
    }

    public ReportDsl verifyProfit(String account, String period, String amount) {
        verifyStat(account, period, amount, ReportFields.PROFIT);
        return this;
    }

    public void verifyCost(String account, String period, String amount) {
        verifyStat(account, period, amount, ReportFields.NET_COST);
    }

    public void verifyDepositsPlusWithdrawals(String account, String period, String amount) {
        verifyStat(account, period, amount, ReportFields.DEPOSITS_PLUS_WITHDRAWALS);
    }

    public void verifyXirrCumulative(String account, String period, String amount) {
        verifyValueStat(account, period, amount, r -> {
            var result = r.stats().get(ReportFields.CUMULATIVE_XIRR);
            var multiplied = result.value().multiply(new BigDecimal(100));
            return new ValueStatDto(Result.success(multiplied), Optional.empty());
        });
    }

    private Optional<StatsV2> getAccountResults(String account, String period) {
        var periodStats = getAccountResults(account).periodStats();
        if (!periodStats.containsKey(period)) {
            throw new RuntimeException("Stats for period `" + period + "` for account `" + account + "` requested but not found. Periods available for this account: "
                                       + periodStats.keySet().stream().sorted().collect(Collectors.joining(", ")));
        }
        return Optional.ofNullable(periodStats.get(period));
    }

    public void verifyXirrNotPresent(String account, String period) {
        var xirr = getStat(account, period, ReportFields.CUMULATIVE_XIRR);
        assertThat(xirr.hasResult())
                .as(() -> "Expected no result but got one: " + xirr.value())
                .isFalse();
    }

    private Optional<StatsV2> getAccountPeriodReturns(String account) {
        return getAccountPeriodReturns(account, TOTAL);
    }

    private Optional<StatsV2> getAccountPeriodReturns(String account, String period) {
        var returnsDslAccountDto = getAccountResults(account);
        return Optional.ofNullable(returnsDslAccountDto.periodStats().get(period));
    }

    private AccountDto2 getAccountResults(String account) {
        var resultDto = getResultDto();
        var first = resultDto.accountDtos().stream()
                .filter(accountReturns -> accountReturns.account().equals(account))
                .findFirst();
        assertThat(first)
                .as("Result expected to contain stats for account `" + account + "` but it doesn't. "
                    + "Accounts that actually have some stats: `"
                    + resultDto.accountDtos().stream().map(AccountDto2::account).collect(Collectors.joining("`, `")) + "`")
                .isPresent();
        return first.get();
    }

    public ReportDsl verifyAccountGainIsPositive(String account, String period) {
        var stat = getStat(account, period, "AGain");
        assertThat(stat.value())
                .usingComparator(BigDecimal::compareTo)
                .isGreaterThan(BigDecimal.ZERO);
        return this;
    }

    public void verifyEndDateIsToday() {
        // TODO
    }

    public void verifyValue(String account, String period, String amount) {
        verifyStat(account, period, amount, ReportFields.CUMULATIVE_VALUE);
    }

    public void setCliOutput() {
        reportOptions.jsonOutput = false;
    }

    private PortfolioStatsDto2 getResultDto() {
        var stdout = cliRunResult.stdOut();
        try {
            return GSON.fromJson(stdout, PortfolioStatsDto2.class);
        } catch (JsonSyntaxException e) {
            throw new RuntimeException("Exception thrown while parsing json: " + stdout, e);
        }
    }

    public ReportDsl runWithArgumentCount(int argCount) {
        var args = new ArrayList<>(IntStream.range(0, argCount)
                .boxed()
                .map(i -> "someArgumentValue")
                .toList());
        cliRunResult = beanvestRunner.run(args);
        return this;
    }

    public ReportDsl verifyNonZeroExitCode() {
        AssertCliExecutionResult.assertExecution(cliRunResult).hasNonZeroStatus();
        return this;
    }

    public ReportDsl verifyUsagePrinted() {
        AssertCliExecutionResult.assertExecution(cliRunResult).hasPrintedUsage();
        return this;
    }

    public void verifyNoWarningsShown() {
        assertThat(cliRunResult.stdErr())
                .isBlank();
    }

    public void verifyWarningsShown(String string) {
        assertThat(cliRunResult.stdErr())
                .contains(string);
    }

    public void verifyCashDelta(String account, String period, String expected) {
        verifyStatDelta(account, period, expected, ReportFields.PERIOD_CASH);
    }

    public void verifyDepositsPlusWithdrawalsDelta(String account, String period, String expectedAmount) {
        verifyStatDelta(account, period, expectedAmount, ReportFields.PERIOD_DEPOSITS_PLUS_WITHDRAWALS);
    }

    public void verifyAccountGainDelta(String account, String period, String expectedAmount) {
        verifyStatDelta(account, period, expectedAmount, ReportFields.ACCOUNT_GAIN_PERIODIC);
    }

    public void verifyDepositsDelta(String account, String period, String expectedAmount) {
        verifyStatDelta(account, period, expectedAmount, ReportFields.PERIOD_DEPOSITS);
    }

    public void verifyWithdrawalsDelta(String account, String period, String expectedAmount) {
        verifyStatDelta(account, period, expectedAmount, ReportFields.PERIOD_WITHDRAWALS);
    }

    public void verifyDividendsDelta(String account, String period, String expectedAmount) {
        verifyStatDelta(account, period, expectedAmount, ReportFields.PERIOD_DIVIDEND);
    }

    public void verifyInterestDelta(String account, String period, String expectedAmount) {
        verifyStatDelta(account, period, expectedAmount, ReportFields.PERIOD_INTEREST);
    }

    public void verifyRealizedGainsDelta(String account, String period, String expectedAmount) {
        verifyStatDelta(account, period, expectedAmount, ReportFields.PERIOD_REALIZED_GAINS);
    }

    public void verifyUnrealizedGainsDelta(String account, String period, String expectedAmount) {
        verifyStatDelta(account, period, expectedAmount, ReportFields.PERIOD_UNREALIZED_GAINS);
    }

    public void verifyValueDelta(String account, String period, String expectedAmount) {
        verifyStatDelta(account, period, expectedAmount, ReportFields.PERIOD_VALUE);
    }

    public void verifyFeesDelta(String account, String period, String expectedAmount) {
        verifyStatDelta(account, period, expectedAmount, "pFees");
    }

    private void verifyStatDelta(String account, String period, String expectedAmount, String statId) {
        var result = getStat(account, period, statId).value();
        assertThat(result)
                .usingComparator(BigDecimal::compareTo)
                .isCloseTo(new BigDecimal(expectedAmount), Offset.offset(new BigDecimal(DEFAULT_OFFSET)));
    }

    private void verifyValueStat(String account, String period, String expectedAmount, Function<StatsV2, ValueStatDto> valueStatExtractor) {
        verifyStat(account, period, expectedAmount, valueStatExtractor, DEFAULT_OFFSET);
    }

    private List<String> getAllAccountsWithStatsInPeriod(String period) {
        var resultDto = getResultDto();
        return resultDto.accountDtos().stream()
                .filter(acc -> acc.periodStats().containsKey(period))
                .map(AccountDto2::account).toList();
    }


    private void verifyStat(String account, String period, String expectedAmount, String columnId) {
        var result = getStat(account, period, columnId).value();

        var expected = new BigDecimal(expectedAmount);
        var slack = Offset.offset(new BigDecimal(DEFAULT_OFFSET));

        assertThat(result)
                .usingComparator(BigDecimal::compareTo)
                .as("Stat `%s` for `%s` in period `%s`".formatted(columnId, account, period))
                .isCloseTo(expected, slack);
    }

    private Result<BigDecimal, StatErrors> getStat(String account, String period, String columnId) {
        var accountResults = getAccountResults(account, period);
        if (accountResults.isEmpty()) {
            throw new RuntimeException("account `%s` has no stats for period `%s`. Only following accounts have stats in that period: %s"
                    .formatted(account, period, getAllAccountsWithStatsInPeriod(period)));
        }
        var stats = accountResults.get().stats();
        if (!stats.containsKey(columnId)) {
            throw new RuntimeException("Account `%s` doesnt have stat `%s` in period `%s`. It has only stats: %s".formatted(account, columnId, period, stats.keySet()));
        }
        var result1 = stats
                .get(columnId);
        return result1;
    }

    @Deprecated //use verifyColumnStat instead
    private void verifyStat(String account, String period, String expectedAmount, Function<StatsV2, ValueStatDto> valueStatExtractor, String defaultOffset) {
        var result = getAccountResults(account, period).get();
        var value = valueStatExtractor.apply(result).stat().value();

        assertThat(value)
                .usingComparator(BigDecimal::compareTo)
                .isCloseTo(new BigDecimal(expectedAmount), Offset.offset(new BigDecimal(defaultOffset)));
    }

    public void verifyXirrPeriodic(String account, String period, String expectedAmount) {
        verifyStat(account, period, expectedAmount, statsWithDeltasDto ->
                new ValueStatDto(
                        statsWithDeltasDto.stats().get(ReportFields.COL_PERIOD_XIRR).map(x -> x.multiply(new BigDecimal(100))),
                        Optional.empty()), "0.5");
    }

    public void verifyDepositsError(String account, String period, String error) {
    }

    public void verifyXirrError(String account, String period, String error) {
        var result = getStat(account, period, ReportFields.CUMULATIVE_XIRR);
        assertThat(result.error().getEnums())
                .isEqualTo(List.of(StatErrorEnum.valueOf(error)));
    }

    public void storeJournal(String file, String content) {
        try {
            var filePath = Path.of(tempDirectory.toString() + "/" + file);
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setReportHoldings() {
        reportOptions.reportInvestments = true;
    }

    public void verifyWithdrawalsError(String s, String total, String s1) {

    }

    public void verifyCashError(String account, String period, String error) {
        var result = getAccountPeriodReturns(account, period).get();
        assertThat(result.stats().get(ReportFields.CUMUALTIVE_CASH).error().getEnums())
                .isEqualTo(List.of(StatErrorEnum.valueOf(error)));
    }

    public void verifyInterestError(String s, String total, String s1) {

    }

    public void cleanUp() {
        testFiles.cleanUp();
    }
}