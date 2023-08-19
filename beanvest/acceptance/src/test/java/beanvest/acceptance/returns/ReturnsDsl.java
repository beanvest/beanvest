package beanvest.acceptance.returns;

import beanvest.BeanvestMain;
import beanvest.lib.apprunner.AppRunner;
import beanvest.lib.apprunner.AppRunnerFactory;
import beanvest.lib.apprunner.CliExecutionResult;
import beanvest.lib.testing.TestFiles;
import beanvest.lib.testing.asserts.AssertCliExecutionResult;
import beanvest.lib.util.gson.GsonFactory;
import beanvest.processor.dto.ValueStatDto;
import beanvest.processor.processingv2.dto.StatsV2;
import beanvest.processor.processingv2.dto.AccountDto2;
import beanvest.processor.processingv2.dto.PortfolioStatsDto2;
import beanvest.result.ErrorEnum;
import beanvest.result.Result;
import beanvest.result.UserErrors;
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
public class ReturnsDsl {
    public static final Gson GSON = GsonFactory.builderWithProjectDefaults().create();

    public static final String TOTAL = "TOTAL";
    public static final String DEFAULT_OFFSET = "0.05";
    public static final String COL_PERIOD_XIRR = "pXirr";
    public static final String CUMULATIVE_XIRR = "Xirr";
    public static final String CUMULATIVE_DIVIDEND = "Div";
    public static final String PERIOD_DIVIDEND = "pDiv";
    public static final String CUMULATIVE_FEES = "Fees";
    public static final String PERIOD_INTEREST = "pIntr";
    public static final String CUMULATIVE_INTEREST = "Intr";
    public static final String WITHDRAWALS = "Wths";
    public static final String PERIOD_WITHDRAWALS = "pWths";
    public static final String PERIOD_DEPOSITS = "pDeps";
    public static final String CUMULATIVE_DEPOSITS = "Deps";
    public static final String CUMULATIVE_REALIZED_GAINS = "RGain";
    public static final String CUMULATIVE_VALUE = "Value";
    public static final String PERIOD_VALUE = "pValue";
    public static final String CUMUALTIVE_CASH = "Cash";
    public static final String PERIOD_CASH = "pCash";
    public static final String PERIOD_REALIZED_GAINS = "pRGain";
    public static final String CUMULATIVE_UNREALIZED_GAINS = "UGain";
    public static final String PERIOD_UNREALIZED_GAINS = "pUGain";
    public static final String NET_COST = "Cost";
    public static final String PROFIT = "Profit";
    private final AppRunner appRunner = AppRunnerFactory.createRunner(BeanvestMain.class, "returns");
    private CliExecutionResult cliRunResult;
    private final CliOptions cliOptions = new CliOptions();
    private Path tempDirectory;
    private TestFiles testFiles = new TestFiles();


    public void verifyOutputIsValidJson() {
        GsonFactory.builderWithProjectDefaults().create().toJsonTree(cliRunResult.stdOut());
    }

    public void setEnd(String endDate) {
        cliOptions.end = endDate;
    }

    public void setStartDate(String startDate) {
        cliOptions.start = startDate;
    }

    public void runCalculateReturnsOnDirectory(String ledgersDir) {
        runCalculateReturnsWithFilesArgs(tempDirectory.toString() + "/" + ledgersDir);
    }

    public void runCalculateReturns(String ledgers) {
        final List<String> allLedgers = writeToTempFiles(ledgers);
        runCalculateReturnsWithFilesArgs(allLedgers.toArray(new String[0]));
    }

    public void runCalculateReturnsWithFilesArgs(String... ledgerFiles) {
        var args = new ArrayList<String>();
        if (cliOptions.end != null) {
            args.add("--end=" + cliOptions.end);
        }
        if (cliOptions.reportInvestments) {
            args.add("--report-holdings");
        }
        if (cliOptions.start != null) {
            args.add("--startDate=" + cliOptions.start);
        }
        args.addAll(List.of(ledgerFiles));
        if (cliOptions.jsonOutput) {
            args.add("--json");
        }
        if (cliOptions.noSecurities) {
            args.add("--no-securities");
        }
        if (cliOptions.account != null) {
            args.add("--account=" + cliOptions.account);
        }
        if (cliOptions.groups == Groups.ONLY) {
            args.add("--groups=only");
        }
        if (cliOptions.groups == Groups.NO) {
            args.add("--groups=no");
        }
        if (cliOptions.onlyFinishedPeriods) {
            args.add("--finished-periods");
        }
        if (cliOptions.delta) {
            args.add("--delta");
        }
        if (cliOptions.interval != null) {
            args.add("--interval=" + cliOptions.interval);
        }
        if (cliOptions.currency != null) {
            args.add("--currency=" + cliOptions.currency);
        }
        if (cliOptions.overrideToday != null) {
            args.add("--override-today=" + cliOptions.overrideToday);
        }
        if (cliOptions.showClosed) {
            args.add("--show-closed");
        }
        if (!cliOptions.columns.isEmpty()) {
            args.add("--columns=" + String.join(",", cliOptions.columns));
        }
        if (cliOptions.allowNonZeroExitCodes) {
            cliRunResult = appRunner.run(args);
        } else {
            cliRunResult = appRunner.runSuccessfully(args);
        }
    }

    public ReturnsDsl verifyStdErrContains(String string) {
        assertThat(cliRunResult.stdErr()).contains(string);
        return this;
    }

    public void setAllowNonZeroExitCodes() {
        cliOptions.allowNonZeroExitCodes = true;
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
        cliOptions.jsonOutput = true;
    }

    public void verifyClosingDate(String account, String expectedClosingDate) {
        var first = getAccountResults(account);
        assertThat(first.closingDate())
                .isEqualTo(Optional.of(LocalDate.parse(expectedClosingDate)));
    }

    public void verifyZeroExitCode() {
        assertThat(cliRunResult.exitCode()).isEqualTo(0);
    }

    public void setAccountFilter(String accountBeginsWith) {
        cliOptions.account = accountBeginsWith;
    }

    public void setYearly() {
        cliOptions.interval = "year";
    }

    public void verifyHasStats(String trading, String period) {
        assertThat(getAccountPeriodReturns(trading, period)).isNotEmpty();
    }

    public void verifyHasStats(String trading) {
        verifyHasStats(trading, TOTAL);
    }

    public void verifyHasNoStats(String trading, String period) {
        var accountReturns = getAccountPeriodReturns(trading, period);
        assertThat(accountReturns).isNotPresent();
    }

    public void verifyHasNoStats(String account) {
        var accountFound = getResultDto().accountDtos().stream().anyMatch(s -> s.account().equals(account));
        assertThat(accountFound)
                .as("account `" + account + "` should not be in the results")
                .isFalse();
    }

    public void setColumns(String... columns) {
        cliOptions.columns = List.of(columns);
    }


    public void verifyResultErrorShown(String account, String period, String error) {
        // TODO figure out general errors eg PRICE NEEDED?
//        var accountReturns = getAccountPeriodReturns(account, period);
//        assertThat(accountReturns)
//                .as(account + "@" + period + " was expected to have no stats")
//                .isEmpty();
//        assertThat(accountReturns.getError().getIds()).contains(UserErrorId.valueOf(error));
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
        cliOptions.groups = Groups.ONLY;
    }

    public void setGroupingDisabled() {
        this.cliOptions.groups = Groups.NO;
    }

    public void verifyFeesTotal(String account, String period, String amount) {
        verifyStat(account, period, amount, CUMULATIVE_FEES);
    }

    public void verifyRealizedGains(String account, String period, String amount) {
        verifyStat(account, period, amount, CUMULATIVE_REALIZED_GAINS);
    }

    public void verifyUnrealizedGains(String account, String period, String amount) {
        verifyStat(account, period, amount, CUMULATIVE_UNREALIZED_GAINS);
    }

    public void verifyDividends(String account, String period, String amount) {
        verifyStat(account, period, amount, CUMULATIVE_DIVIDEND);
    }

    public void verifyCash(String account, String period, String amount) {
        verifyStat(account, period, amount, CUMUALTIVE_CASH);
    }

    public void setDeltas() {
        cliOptions.delta = true;
    }

    public void verifyDeposits(String account, String period, String amount) {
        verifyStat(account, period, amount, CUMULATIVE_DEPOSITS);
    }

    public void verifyWithdrawals(String account, String period, String amount) {
        verifyStat(account, period, amount, WITHDRAWALS);
    }

    public void verifyInterest(String account, String period, String amount) {
        this.verifyStat(account, period, amount, CUMULATIVE_INTEREST);
    }

    public void setGroupingEnabled() {
        cliOptions.groups = Groups.YES;
    }

    public void setQuarterly() {
        cliOptions.interval = "quarter";
    }

    public void setMonthly() {
        cliOptions.interval = "month";
    }

    public void setCurrency(String currency) {
        cliOptions.currency = currency;
    }

    public void setCurrentDate(String s) {
        cliOptions.overrideToday = s;
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

    public ReturnsDsl verifyProfit(String account, String period, String amount) {
        verifyStat(account, period, amount, PROFIT);
        return this;
    }

    public void verifyCost(String account, String period, String amount) {
        verifyStat(account, period, amount, NET_COST);
    }

    public void verifyXirrCumulative(String account, String period, String amount) {
        verifyValueStat(account, period, amount, r -> {
            var result = r.stats().get(CUMULATIVE_XIRR);
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
        var xirr = getAccountResults(account, period).get().stats().get("xirr");
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

    public ReturnsDsl verifyGainIsPositive(String account) {
        var result = getAccountPeriodReturns(account).get();
        assertThat(result.stats().get("aGain").value())
                .usingComparator(BigDecimal::compareTo)
                .isGreaterThan(BigDecimal.ZERO);
        return this;
    }

    public void verifyEndDateIsToday() {
        // TODO
    }

    public void verifyValue(String account, String period, String amount) {
        verifyStat(account, period, amount, CUMULATIVE_VALUE);
    }

    public void setCliOutput() {
        cliOptions.jsonOutput = false;
    }

    private PortfolioStatsDto2 getResultDto() {
        var stdout = cliRunResult.stdOut();
        try {
            return GSON.fromJson(stdout, PortfolioStatsDto2.class);
        } catch (JsonSyntaxException e) {
            throw new RuntimeException("Exception thrown while parsing json: " + stdout, e);
        }
    }

    public ReturnsDsl runWithArgumentCount(int argCount) {
        var args = new ArrayList<>(IntStream.range(0, argCount)
                .boxed()
                .map(i -> "someArgumentValue")
                .toList());
        cliRunResult = appRunner.run(args);
        return this;
    }

    public ReturnsDsl verifyNonZeroExitCode() {
        AssertCliExecutionResult.assertExecution(cliRunResult).hasNonZeroStatus();
        return this;
    }

    public ReturnsDsl verifyUsagePrinted() {
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
        verifyStatDelta(account, period, expected, PERIOD_CASH);
    }


    public void verifyAccountGainDelta(String account, String period, String expectedAmount) {
        verifyStatDelta(account, period, expectedAmount, r -> null);
    }

    public void verifyDepositsDelta(String account, String period, String expectedAmount) {
        verifyStatDelta(account, period, expectedAmount, PERIOD_DEPOSITS);
    }

    public void verifyWithdrawalsDelta(String account, String period, String expectedAmount) {
        verifyStatDelta(account, period, expectedAmount, PERIOD_WITHDRAWALS);
    }

    public void verifyDividendsDelta(String account, String period, String expectedAmount) {
        verifyStatDelta(account, period, expectedAmount, PERIOD_DIVIDEND);
    }

    public void verifyInterestDelta(String account, String period, String expectedAmount) {
        verifyStatDelta(account, period, expectedAmount, PERIOD_INTEREST);
    }

    public void verifyRealizedGainsDelta(String account, String period, String expectedAmount) {
        verifyStatDelta(account, period, expectedAmount, PERIOD_REALIZED_GAINS);
    }

    public void verifyUnrealizedGainsDelta(String account, String period, String expectedAmount) {
        verifyStatDelta(account, period, expectedAmount, PERIOD_UNREALIZED_GAINS);
    }

    public void verifyValueDelta(String account, String period, String expectedAmount) {
        verifyStatDelta(account, period, expectedAmount, PERIOD_VALUE);
    }

    public void verifyFeesDelta(String account, String period, String expectedAmount) {
        verifyStatDelta(account, period, expectedAmount, "pFees");
    }

    private void verifyStatDelta(String account, String period, String expectedAmount, String statId) {
        var result = getAccountResults(account, period).get().stats().get(statId).value();
        assertThat(result)
                .usingComparator(BigDecimal::compareTo)
                .isCloseTo(new BigDecimal(expectedAmount), Offset.offset(new BigDecimal(DEFAULT_OFFSET)));
    }

    @Deprecated //use verifyStatDelta instead
    private void verifyStatDelta(String account, String period, String expectedAmount, Function<StatsV2, Optional<BigDecimal>> statExtractor) {
        var result = getAccountResults(account, period).get();
        var actual = statExtractor.apply(result).get();
        assertThat(actual)
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

        var result = result1.value();

        var expected = new BigDecimal(expectedAmount);
        var slack = Offset.offset(new BigDecimal(DEFAULT_OFFSET));

        assertThat(result)
                .usingComparator(BigDecimal::compareTo)
                .as("Stat `%s` for `%s` in period `%s`".formatted(columnId, account, period))
                .isCloseTo(expected, slack);
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
                        statsWithDeltasDto.stats().get(COL_PERIOD_XIRR).map(x -> x.multiply(new BigDecimal(100))),
                        Optional.empty()), "0.5");
    }

    public void verifyDepositsError(String account, String period, String error) {
    }

    public void verifyXirrError(String account, String period, String error) {
        var result = getAccountPeriodReturns(account, period).get();
        assertThat(result.stats().get("xirr").error().getEnums())
                .isEqualTo(List.of(ErrorEnum.valueOf(error)));
    }

    public void storeJournal(String file, String content) {
        if (tempDirectory == null) {
            tempDirectory = testFiles.createTempDirectory();
        }
        try {
            var filePath = Path.of(tempDirectory.toString() + "/" + file);
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setReportHoldings() {
        cliOptions.reportInvestments = true;
    }

    public void verifyWithdrawalsError(String s, String total, String s1) {

    }

    public void verifyCashError(String account, String period, String error) {
        var result = getAccountPeriodReturns(account, period).get();
        assertThat(result.stats().get(CUMUALTIVE_CASH).error().getEnums())
                .isEqualTo(List.of(ErrorEnum.valueOf(error)));
    }

    public void verifyInterestError(String s, String total, String s1) {

    }

    enum Groups {
        YES,
        NO,
        ONLY
    }

    public void cleanUp() {
        testFiles.cleanUp();
    }

    static class CliOptions {

        public Groups groups;
        public boolean onlyFinishedPeriods = false;
        public boolean delta = false;
        public String interval = null;
        public boolean showClosed = false;
        public String currency;
        public String overrideToday;
        public boolean reportInvestments;
        List<String> columns = new ArrayList<>();
        String account;
        String end;
        String start;
        boolean allowNonZeroExitCodes = false;
        boolean jsonOutput = true;
        boolean noSecurities = false;
    }
}