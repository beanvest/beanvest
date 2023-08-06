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
import beanvest.processor.processingv2.processor.PeriodDividendCalculator;
import beanvest.processor.processingv2.processor.RealizedGainCalculator;
import beanvest.processor.processingv2.processor.WithdrawalCalculator;

public enum ColumnId {
    ACCOUNT("Account", "account or group", null),
    OPENED("Opened", "opening date", null),
    CLOSED("Closed", "closing date", null),
    DEPOSITS("Deps", "deposits", DepositsCalculator.class),
    DEPOSITS_PERIOD("pDeps", "deposits per period", PeriodDepositCalculator.class),
    WITHDRAWALS("Wths", "withdrawals", WithdrawalCalculator.class),
    WITHDRAWALS_PERIOD("pWths", "withdrawals per period", PeriodWithdrawalCalculator.class),
    INTEREST("Intr", "interest", InterestCalculator.class),
    INTEREST_PERIOD("pIntr", "interest per period", PeriodInterestCalculator.class),
    FEES("Fees", "platform and transaction fees", FeesCalculator.class),
    FEES_PERIOD("pFees", "platform and transaction fees per period", PeriodFeeCalculator.class),
    XIRR("Xirr", "internal rate of return (cumulative)", null),
    XIRR_PERIOD("pXirr", "periodic (periodic)", null),
    REALIZED_GAIN("RGain", "realized gain", RealizedGainCalculator.class),
    REALIZED_GAIN_PERIOD("pRGain", "realized gain per period", PeriodRealizedGainCalculator.class),
    UNREALIZED_GAIN("UGain", "unrealized gain", null),
    UNREALIZED_GAIN_PERIOD("pUGain", "unrealized gain per period", null),
    DIVIDENDS("Div", "dividends cumulative", DividendCalculator.class),
    DIVIDENDS_PERIOD("pDiv", "dividends per period", PeriodDividendCalculator.class),
    PROFIT("AGain", "holdings value + cash + withdrawals - deposits", null),
    PROFIT_PERIOD("pAGain", "holdings value + cash + withdrawals - deposits", null),
    CASH("Cash", "cash", CashCalculator.class),
    CASH_PERIOD("pCash", "cash per period", PeriodCashCalculator.class),
    VALUE("Value", "cash + market value of the holdings", null),
    VALUE_PERIOD("pValue", "cash + market value of the holdings", null)
    ;

    public final String header;
    public final String name;
    public final Class<?> calculator;

    ColumnId(String header, String name, Class<?> calculator) {
        this.header = header;
        this.name = name;
        this.calculator = calculator;
    }
}
