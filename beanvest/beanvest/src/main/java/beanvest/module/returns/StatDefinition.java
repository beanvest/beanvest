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
    OPENED("Opened", "opening date", null, StatType.ACCOUNT),
    CLOSED("Closed", "closing date", null, StatType.ACCOUNT),
    DEPOSITS("Deps", "deposits", DepositsCalculator.class, StatType.CUMULATIVE),
    DEPOSITS_PERIOD("pDeps", "deposits per period", PeriodDepositCalculator.class, StatType.PERIODIC),
    WITHDRAWALS("Wths", "withdrawals", WithdrawalCalculator.class, StatType.CUMULATIVE),
    WITHDRAWALS_PERIOD("pWths", "withdrawals per period", PeriodWithdrawalCalculator.class, StatType.PERIODIC),
    INTEREST("Intr", "interest", InterestCalculator.class, StatType.CUMULATIVE),
    INTEREST_PERIOD("pIntr", "interest per period", PeriodInterestCalculator.class, StatType.PERIODIC),
    FEES("Fees", "platform and transaction fees", FeesCalculator.class, StatType.CUMULATIVE),
    FEES_PERIOD("pFees", "platform and transaction fees per period", PeriodFeeCalculator.class, StatType.PERIODIC),
    XIRR("Xirr", "internal rate of return (cumulative)", XirrCalculator.class, StatType.CUMULATIVE),
    XIRR_PERIOD("pXirr", "periodic (periodic)", PeriodXirrCalculator.class, StatType.PERIODIC),
    REALIZED_GAIN("RGain", "realized gain", RealizedGainCalculator.class, StatType.CUMULATIVE),
    REALIZED_GAIN_PERIOD("pRGain", "realized gain per period", PeriodRealizedGainCalculator.class, StatType.PERIODIC),
    UNREALIZED_GAIN("UGain", "unrealized gain", UnrealizedGainCalculator.class, StatType.CUMULATIVE),
    UNREALIZED_GAIN_PERIOD("pUGain", "unrealized gain per period", PeriodUnrealizedGainCalculator.class, StatType.PERIODIC),
    DIVIDENDS("Div", "dividends cumulative", DividendCalculator.class, StatType.CUMULATIVE),
    DIVIDENDS_PERIOD("pDiv", "dividends per period", PeriodDividendCalculator.class, StatType.PERIODIC),
    PROFIT("Profit", "value - cost", ProfitCalculator.class, StatType.CUMULATIVE),
    PROFIT_PERIOD("pProfit", "holdings value + cash + withdrawals - deposits", null, StatType.PERIODIC),
    CASH("Cash", "cash", CashCalculator.class, StatType.CUMULATIVE),
    CASH_PERIOD("pCash", "cash per period", PeriodCashCalculator.class, StatType.PERIODIC),
    NET_COST("Cost", "total cost of account or holding", NetCostCalculator.class, StatType.CUMULATIVE),
    VALUE("Value", "cash + market value of the holdings", ValueCalculator.class, StatType.CUMULATIVE),
    VALUE_PERIOD("pValue", "cash + market value of the holdings", PeriodValueCalculator.class, StatType.PERIODIC);

    public final String header;
    public final String name;
    public final Class<?> calculator;
    public final StatType type;

    StatDefinition(String shortName, String description, Class<?> calculator, StatType type) {
        this.header = shortName;
        this.name = description;
        this.calculator = calculator;
        this.type = type;
    }

    public enum StatType {
        ACCOUNT,
        PERIODIC,
        CUMULATIVE
    }
}
