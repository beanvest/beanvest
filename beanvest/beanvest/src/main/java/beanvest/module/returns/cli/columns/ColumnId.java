package beanvest.module.returns.cli.columns;

import beanvest.processor.processingv2.PeriodInterestCalculator;
import beanvest.processor.processingv2.processor.CashCalculator;
import beanvest.processor.processingv2.processor.DepositsCalculator;
import beanvest.processor.processingv2.processor.DividendCalculator;
import beanvest.processor.processingv2.processor.FeesCalculator;
import beanvest.processor.processingv2.processor.InterestCalculator;
import beanvest.processor.processingv2.processor.PeriodCashCalculator;
import beanvest.processor.processingv2.processor.PeriodDepositCalculator;
import beanvest.processor.processingv2.processor.PeriodFeeCalculator;
import beanvest.processor.processingv2.processor.PeriodRealizedGainCalculator;
import beanvest.processor.processingv2.processor.PeriodWithdrawalCalculator;
import beanvest.processor.processingv2.processor.PlatformFeeCalculator;
import beanvest.processor.processingv2.processor.PeriodDividendCalculator;
import beanvest.processor.processingv2.processor.RealizedGainCalculator;
import beanvest.processor.processingv2.processor.WithdrawalCalculator;

public enum ColumnId {
    ACCOUNT("account", "account or group", null),
    OPENED("opened", "opening date", null),
    CLOSED("closed", "closing date", null),
    DEPOSITS("cDeps", "deposits", DepositsCalculator.class),
    DEPOSITS_PERIOD("pDeps", "deposits per period", PeriodDepositCalculator.class),
    WITHDRAWALS("cWths", "withdrawals", WithdrawalCalculator.class),
    WITHDRAWALS_PERIOD("pWths", "withdrawals per period", PeriodWithdrawalCalculator.class),
    DEPOSITS_AND_WITHDRAWALS("dw", "deposits plus withdrawals", null),
    INTEREST("cIntr", "interest", InterestCalculator.class),
    INTEREST_PERIOD("pIntr", "interest per period", PeriodInterestCalculator.class),
    FEES("cFees", "platform and transaction fees", FeesCalculator.class),
    FEES_PERIOD("pFees", "platform and transaction fees per period", PeriodFeeCalculator.class),
    INTEREST_FEES("if", "interest plus fees", null),
    HOLDINGS_VALUE("hVal", "holdings value", null),
    XIRR("xirr", "internal rate of return (cumulative)", null),
    XIRR_PERIOD("xirrp", "periodic (periodic)", null),
    REALIZED_GAIN("cReGa", "realized gain", RealizedGainCalculator.class),
    REALIZED_GAIN_PERIOD("pReGa", "realized gain per period", PeriodRealizedGainCalculator.class),
    UNREALIZED_GAIN("uGain", "unrealized gain", null),
    DIVIDENDS("cDiv", "dividends cumulative", DividendCalculator.class),
    DIVIDENDS_PERIOD("pDiv", "dividends per period", PeriodDividendCalculator.class),
    ACCOUNT_GAIN("aGain", "holdings value + cash + withdrawals - deposits", null),
    CASH("cCash", "cash", CashCalculator.class),
    CASH_PERIOD("pCash", "cash per period", PeriodCashCalculator.class),
    VALUE("value", "cash + market value of the holdings", null);

    public final String header;
    public final String name;
    public final Class<?> calculator;

    ColumnId(String header, String name, Class<?> calculator) {
        this.header = header;
        this.name = name;
        this.calculator = calculator;
    }
}
