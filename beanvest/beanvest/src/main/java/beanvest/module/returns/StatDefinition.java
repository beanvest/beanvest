package beanvest.module.returns;

import beanvest.processor.processingv2.ValueCalculator;
import beanvest.processor.processingv2.processor.NetCostCalculator;
import beanvest.processor.processingv2.processor.ProfitCalculator;
import beanvest.processor.processingv2.processor.periodic.PeriodUnrealizedGainCalculator;
import beanvest.processor.processingv2.processor.periodic.PeriodValueCalculator;
import beanvest.processor.processingv2.processor.periodic.PeriodXirrCalculator;
import beanvest.processor.processingv2.processor.UnrealizedGainCalculator;
import beanvest.processor.processingv2.processor.periodic.PeriodInterestCalculator;
import beanvest.processor.processingv2.processor.CashCalculator;
import beanvest.processor.processingv2.processor.DepositsCalculator;
import beanvest.processor.processingv2.processor.DividendCalculator;
import beanvest.processor.processingv2.processor.FeesCalculator;
import beanvest.processor.processingv2.processor.InterestCalculator;
import beanvest.processor.processingv2.processor.periodic.PeriodCashCalculator;
import beanvest.processor.processingv2.processor.periodic.PeriodDepositCalculator;
import beanvest.processor.processingv2.processor.periodic.PeriodFeeCalculator;
import beanvest.processor.processingv2.processor.periodic.PeriodRealizedGainCalculator;
import beanvest.processor.processingv2.processor.periodic.PeriodWithdrawalCalculator;
import beanvest.processor.processingv2.processor.periodic.PeriodDividendCalculator;
import beanvest.processor.processingv2.processor.RealizedGainCalculator;
import beanvest.processor.processingv2.processor.WithdrawalCalculator;
import beanvest.processor.processingv2.processor.XirrCalculator;

public enum StatDefinition {
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
    XIRR("Xirr", "internal rate of return (cumulative)", XirrCalculator.class),
    XIRR_PERIOD("pXirr", "periodic (periodic)", PeriodXirrCalculator.class),
    REALIZED_GAIN("RGain", "realized gain", RealizedGainCalculator.class),
    REALIZED_GAIN_PERIOD("pRGain", "realized gain per period", PeriodRealizedGainCalculator.class),
    UNREALIZED_GAIN("UGain", "unrealized gain", UnrealizedGainCalculator.class),
    UNREALIZED_GAIN_PERIOD("pUGain", "unrealized gain per period", PeriodUnrealizedGainCalculator.class),
    DIVIDENDS("Div", "dividends cumulative", DividendCalculator.class),
    DIVIDENDS_PERIOD("pDiv", "dividends per period", PeriodDividendCalculator.class),
    PROFIT("Profit", "value - cost", ProfitCalculator.class),
    PROFIT_PERIOD("pProfit", "holdings value + cash + withdrawals - deposits", null),
    CASH("Cash", "cash", CashCalculator.class),
    CASH_PERIOD("pCash", "cash per period", PeriodCashCalculator.class),
    NET_COST("Cost", "total cost of account or holding", NetCostCalculator.class),
    VALUE("Value", "cash + market value of the holdings", ValueCalculator.class),
    VALUE_PERIOD("pValue", "cash + market value of the holdings", PeriodValueCalculator.class)
    ;

    public final String header;
    public final String name;
    public final Class<?> calculator;

    StatDefinition(String shortName, String description, Class<?> calculator) {
        this.header = shortName;
        this.name = description;
        this.calculator = calculator;
    }
}
