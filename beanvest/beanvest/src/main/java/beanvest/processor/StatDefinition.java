package beanvest.processor;

import beanvest.processor.processingv2.CurrencyConverter;
import beanvest.processor.processingv2.ValueCalculator;
import beanvest.processor.processingv2.processor.*;
import beanvest.processor.processingv2.processor.periodic.*;

public enum StatDefinition {
    DEPOSITS("Deps", "deposits", DepositsCalculator.class, StatType.CUMULATIVE),
    DEPOSITS_PERIOD("pDeps", "deposits per period", PeriodDepositCalculator.class, StatType.PERIODIC),
    WITHDRAWALS("Wths", "withdrawals", WithdrawalCalculator.class, StatType.CUMULATIVE),
    WITHDRAWALS_PERIOD("pWths", "withdrawals per period", PeriodWithdrawalCalculator.class, StatType.PERIODIC),
    DEPOSITS_AND_WITHDRAWALS("DW", "deposits plus withdrawals", DepositsPlusWithdrawalsCalculator.class, StatType.CUMULATIVE),
    DEPOSITS_AND_WITHDRAWALS_PERIOD("pDW", "deposits plus withdrawals", PeriodDepositsPlusWithdrawalsCalculator.class, StatType.PERIODIC),
    INTEREST("Intr", "interest", InterestCalculator.class, StatType.CUMULATIVE),
    INTEREST_PERIOD("pIntr", "interest per period", PeriodInterestCalculator.class, StatType.PERIODIC),
    FEES("Fees", "platform and transaction fees", FeesCalculator.class, StatType.CUMULATIVE),
    FEES_PERIOD("pFees", "platform and transaction fees per period", PeriodFeeCalculator.class, StatType.PERIODIC),
    XIRR("Xirr", "internal rate of return (cumulative)", XirrCalculator.class, StatType.CUMULATIVE),
    XIRR_PERIOD("pXirr", "periodic (periodic)", PeriodXirrCalculator.class, StatType.PERIODIC),
    REALIZED_GAIN("RGain", "realized gain", RealizedGainCalculator.class, StatType.CUMULATIVE),
    REALIZED_GAIN_PERIOD("pRGain", "realized gain per period", PeriodRealizedGainCalculator.class, StatType.PERIODIC),
    CURRENCY_GAIN("CGain", "currency gain", CurrencyGainCalculator.class, StatType.CUMULATIVE),
    CURRENCY_GAIN_PERIOD("pCGain", "currency gain per period", PeriodCurrencyGainCalculator.class, StatType.PERIODIC),
    UNREALIZED_GAIN("UGain", "unrealized gain", UnrealizedGainCalculator.class, StatType.CUMULATIVE),
    UNREALIZED_GAIN_PERIOD("pUGain", "unrealized gain per period", PeriodUnrealizedGainCalculator.class, StatType.PERIODIC),
    ACCOUNT_GAIN("AGain", "unrealized gain + realized gain + interest + dividends - fees", AccountGainCalculator.class, StatType.CUMULATIVE),
    ACCOUNT_GAIN_PERIOD("pAGain", "unrealized gain + realized gain + interest + dividends - fees per period", PeriodAccountGainCalculator.class, StatType.PERIODIC),
    DIVIDENDS("Div", "dividends cumulative", DividendCalculator.class, StatType.CUMULATIVE),
    DIVIDENDS_PERIOD("pDiv", "dividends per period", PeriodDividendCalculator.class, StatType.PERIODIC),
    PROFIT("Profit", "value - cost", ProfitCalculator.class, StatType.CUMULATIVE),
    PROFIT_PERIOD("pProfit", "holdings value + cash + withdrawals - deposits", null, StatType.PERIODIC),
    CASH("Cash", "cash", CashCalculator.class, StatType.CUMULATIVE),
    CASH_PERIOD("pCash", "cash per period", PeriodCashCalculator.class, StatType.PERIODIC),
    NET_COST("Cost", "total cost of account or holding", NetCostCalculator.class, StatType.CUMULATIVE),
    VALUE("Value", "cash + market value of the holdings", ValueCalculator.class, StatType.CUMULATIVE),
    VALUE_PERIOD("pValue", "cash + market value of the holdings", PeriodValueCalculator.class, StatType.PERIODIC);

    public final String shortName;
    public final String description;
    public final Class<?> calculator;
    public final StatType type;

    StatDefinition(String shortName, String description, Class<?> calculator, StatType type) {
        this.shortName = shortName;
        this.description = description;
        this.calculator = calculator;
        this.type = type;
    }

    public enum StatType {
        ACCOUNT,
        PERIODIC,
        CUMULATIVE
    }
}
