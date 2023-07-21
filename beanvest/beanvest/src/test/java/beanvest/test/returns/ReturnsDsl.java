package beanvest.test.returns;

import beanvest.BeanvestMain;
import beanvest.lib.testing.AppRunner;
import beanvest.lib.testing.AppRunnerFactory;
import beanvest.lib.testing.CliExecutionResult;
import beanvest.lib.testing.TestFiles;
import beanvest.lib.testing.asserts.AssertCliExecutionResult;
import beanvest.lib.util.gson.GsonFactory;
import beanvest.tradingjournal.AccountDto;
import beanvest.tradingjournal.PortfolioStats;
import beanvest.tradingjournal.Result;
import beanvest.tradingjournal.Stat;
import beanvest.tradingjournal.StatsWithDeltas;
import beanvest.tradingjournal.ValueStat;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.assertj.core.data.Offset;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
    private final AppRunner appRunner = AppRunnerFactory.createRunner(BeanvestMain.class, "returns");
    private CliExecutionResult cliRunResult;
    private final CliOptions cliOptions = new CliOptions();


    public void verifyOutputIsValidJson() {
        GsonFactory.builderWithProjectDefaults().create().toJsonTree(cliRunResult.stdOut());
    }

    public void setEnd(String endDate) {
        cliOptions.end = endDate;
    }

    public void setStartDate(String startDate) {
        cliOptions.start = startDate;
    }

    public void runCalculateReturns(String... ledgers) {
        final List<String> allLedgers = writeToTempFiles(ledgers);
        runCalculateReturnsWithFilesArgs(allLedgers.toArray(new String[0]));
    }

    public void runCalculateReturnsWithFilesArgs(String... ledgerFiles) {
        var args = new ArrayList<String>();
        if (cliOptions.end != null) {
            args.add("--end=" + cliOptions.end);
        }
        if (cliOptions.start != null) {
            args.add("--start=" + cliOptions.start);
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
        if (cliOptions.noAccounts) {
            args.add("--no-accounts");
        }
        if (cliOptions.onlyFinishedPeriods) {
            args.add("--finished-periods");
        }
        if (cliOptions.delta) {
            args.add("--delta");
        }
        if (cliOptions.group) {
            args.add("--group");
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
        if (cliOptions.exact) {
            args.add("--exact");
        }
        if (cliOptions.columns.size() > 0) {
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
        final AccountDto first = getAccountResults(account);
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
        var accountFound = getResultDto().stats.stream().anyMatch(s -> s.account.equals(account));
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
        assertThat(getAccountResults(account).openingDate).isEqualTo(LocalDate.parse(openingDate));
    }

    public void verifyAccountClosingDate(String account, String closingDate) {
        var expected = Optional.ofNullable(closingDate == null ? null : LocalDate.parse(closingDate));
        assertThat(getAccountResults(account).closingDate)
                .isEqualTo(expected);
    }

    public void verifyResultsReturnedForAccount(String account) {
        assertThat(isAccountInResults(account)).isTrue();
    }

    public void setNoAccounts() {
        cliOptions.noAccounts = true;
    }

    public void verifyFeesTotal(String account, String period, String amount) {
        verifyStat(account, period, amount, StatsWithDeltas::fees);
    }

    public void verifyRealizedGains(String account, String period, String amount) {
        verifyStat(account, period, amount, StatsWithDeltas::realizedGain);
    }

    public void verifyUnrealizedGains(String account, String period, String amount) {
        verifyValueStat(account, period, amount, StatsWithDeltas::unrealizedGains);
    }

    public void verifyDividends(String account, String period, String amount) {
        verifyStat(account, period, amount, StatsWithDeltas::dividends);
    }

    public void verifyCash(String account, String period, String amount) {
        verifyStat(account, period, amount, StatsWithDeltas::cash);
    }

    public void setDeltas() {
        cliOptions.delta = true;
    }

    public void setExact() {
        cliOptions.exact = true;
    }

    public void verifyDeposits(String account, String period, String amount) {
        verifyStat(account, period, amount, StatsWithDeltas::deposits);
    }

    public void verifyWithdrawals(String account, String period, String amount) {
        verifyStat(account, period, amount, StatsWithDeltas::withdrawals);
    }

    public void verifyInterest(String account, String period, String amount) {
        verifyStat(account, period, amount, StatsWithDeltas::interest);
    }

    public void setGroup() {
        cliOptions.group = true;
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
        return getResultDto().stats.stream()
                .anyMatch(accountReturns -> accountReturns.account().equals(account));
    }

    public void verifyResultsNotReturnedForAccount(String account) {
        assertThat(isAccountInResults(account)).isFalse();
    }


    private List<String> writeToTempFiles(String[] ledgers) {
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

    public ReturnsDsl verifyAccountGain(String account, String period, String amount) {
        verifyValueStat(account, period, amount, StatsWithDeltas::accountGain);
        return this;
    }

    public void verifyXirrCumulative(String account, String period, String amount) {
        verifyValueStat(account, period, amount, r -> {
            var multiplied = r.xirr().stat().getValue().multiply(new BigDecimal(100));
            return new ValueStat(Result.success(multiplied), Optional.empty());
        });
    }

    private Optional<StatsWithDeltas> getAccountResults(String account, String period) {
        var periodStats = getAccountResults(account).periodStats;
        if (!periodStats.containsKey(period)) {
            throw new RuntimeException("Stats for period `" + period + "` for account `" + account + "` requested but not found. Periods available for this account: "
                                       + periodStats.keySet().stream().sorted().collect(Collectors.joining(", ")));
        }
        return Optional.ofNullable(periodStats.get(period));
    }

    public void verifyXirrNotPresent(String account, String period) {
        var xirr = getAccountResults(account, period).get().xirr();
        assertThat(xirr.stat().hasResult())
                .as(() -> "Expected no result but got one: " + xirr.stat().getValue())
                .isFalse();
    }

    private Optional<StatsWithDeltas> getAccountPeriodReturns(String account) {
        return getAccountPeriodReturns(account, TOTAL);
    }

    private Optional<StatsWithDeltas> getAccountPeriodReturns(String account, String period) {
        final AccountDto returnsDslAccountDto = getAccountResults(account);
        return Optional.ofNullable(returnsDslAccountDto.periodStats().get(period));
    }

    private AccountDto getAccountResults(String account) {
        var resultDto = getResultDto();
        var first = resultDto.stats.stream()
                .filter(accountReturns -> accountReturns.account().equals(account))
                .findFirst();
        assertThat(first)
                .as("Result expected to contain stats for account `" + account + "` but it doesn't. "
                    + "Accounts that actually have some stats: `"
                    + resultDto.stats.stream().map(s -> s.account).collect(Collectors.joining("`, `")) + "`")
                .isPresent();
        return first.get();
    }

    public ReturnsDsl verifyGainIsPositive(String account) {
        var result = getAccountPeriodReturns(account).get();
        assertThat(result.accountGain().stat().getValue())
                .usingComparator(BigDecimal::compareTo)
                .isGreaterThan(BigDecimal.ZERO);
        return this;
    }

    public void verifyEndDateIsToday() {
        // TODO
    }

    public void verifyValue(String account, String period, String amount) {
        verifyValueStat(account, period, amount, StatsWithDeltas::accountValue);
    }

    public void setCliOutput() {
        cliOptions.jsonOutput = false;
    }

    private PortfolioStats getResultDto() {
        var stdout = cliRunResult.stdOut();
        try {
            return GSON.fromJson(stdout, PortfolioStats.class);
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
        verifyStatDelta(account, period, expected, r -> r.cash().delta());
    }

    public void verifyAccountGainDelta(String account, String period, String expectedAmount) {
        verifyStatDelta(account, period, expectedAmount, r -> r.accountGain().delta());
    }

    public void verifyDepositsDelta(String account, String period, String expectedAmount) {
        verifyStatDelta(account, period, expectedAmount, r -> r.deposits().delta());
    }

    public void verifyWithdrawalsDelta(String account, String period, String expectedAmount) {
        verifyStatDelta(account, period, expectedAmount, r -> r.withdrawals().delta());
    }

    public void verifyDividendsDelta(String account, String period, String expectedAmount) {
        verifyStatDelta(account, period, expectedAmount, r -> r.dividends().delta());
    }

    public void verifyInterestDelta(String account, String period, String expectedAmount) {
        verifyStatDelta(account, period, expectedAmount, r -> r.interest().delta());
    }

    public void verifyRealizedGainsDelta(String account, String period, String expectedAmount) {
        verifyStatDelta(account, period, expectedAmount, r -> r.realizedGain().delta());
    }

    public void verifyUnrealizedGainsDelta(String account, String period, String expectedAmount) {
        verifyStatDelta(account, period, expectedAmount, r -> r.unrealizedGains().delta());
    }

    public void verifyAccountValueDelta(String account, String period, String expectedAmount) {
        verifyStatDelta(account, period, expectedAmount, r -> r.accountValue().delta());
    }

    public void verifyFeesDelta(String account, String period, String expectedAmount) {
        verifyStatDelta(account, period, expectedAmount, r -> r.fees().delta());
    }

    private void verifyStatDelta(String account, String period, String expectedAmount, Function<StatsWithDeltas, Optional<BigDecimal>> statExtractor) {
        var result = getAccountResults(account, period).get();
        var actual = statExtractor.apply(result).get();
        assertThat(actual)
                .usingComparator(BigDecimal::compareTo)
                .isCloseTo(new BigDecimal(expectedAmount), Offset.offset(new BigDecimal(DEFAULT_OFFSET)));
    }

    private void verifyValueStat(String account, String period, String expectedAmount, Function<StatsWithDeltas, ValueStat> valueStatExtractor) {
        var result = getAccountResults(account, period).get();
        var value = valueStatExtractor.apply(result).stat().getValue();
        assertThat(value)
                .usingComparator(BigDecimal::compareTo)
                .isCloseTo(new BigDecimal(expectedAmount), Offset.offset(new BigDecimal(DEFAULT_OFFSET)));
    }

    private void verifyStat(String account, String period, String expectedAmount, Function<StatsWithDeltas, Stat> valueStatExtractor) {
        var result = getAccountResults(account, period).get();
        var value = valueStatExtractor.apply(result).stat();
        assertThat(value)
                .usingComparator(BigDecimal::compareTo)
                .isCloseTo(new BigDecimal(expectedAmount), Offset.offset(new BigDecimal(DEFAULT_OFFSET)));
    }

    public void verifyXirrPeriodic(String account, String period, String expectedAmount) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    static class CliOptions {

        public boolean noAccounts;
        public boolean onlyFinishedPeriods = false;
        public boolean delta = false;
        public boolean exact = false;
        public boolean group = false;
        public String interval = null;
        public boolean showClosed = false;
        public String currency;
        public String overrideToday;
        List<String> columns = new ArrayList<>();
        String account;
        String end;
        String start;
        boolean allowNonZeroExitCodes = false;
        boolean jsonOutput = true;
        boolean noSecurities = false;
    }
}