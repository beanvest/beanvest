package beanvest.acceptance.report.dsl;

import java.util.ArrayList;
import java.util.List;

class ReportOptions {
    //cli only
    public Groups groups;
    public boolean reportInvestments;
    boolean noSecurities = false;
    boolean jsonOutput = true;


    boolean allowNonZeroExitCodes = false;
    public boolean onlyFinishedPeriods = false;
    public boolean delta = false;
    public String interval = null;
    public boolean showClosed = false;
    public String currency;
    public String overrideToday;
    List<String> columns = new ArrayList<>();
    String account;
    String end;
    String start;

    public enum Groups {
        YES,
        NO,
        ONLY
    }
}
