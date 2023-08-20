package beanvest.processor.deprecated.dto;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class AccountDto {
    public final String account;
    public final LocalDate openingDate;
    public final Optional<LocalDate> closingDate;
    public final Map<String, StatsWithDeltasDto> periodStats;

    public AccountDto(String account, LocalDate openingDate, Optional<LocalDate> closingDate,
                      Map<String, StatsWithDeltasDto> periodStats) {
        this.account = account;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
        this.periodStats = periodStats;
    }

    public String account() {
        return account;
    }

    public LocalDate openingDate() {
        return openingDate;
    }

    public Optional<LocalDate> closingDate() {
        return closingDate;
    }

    public Map<String, StatsWithDeltasDto> periodStats() {
        return periodStats;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AccountDto) obj;
        return Objects.equals(this.account, that.account) &&
                Objects.equals(this.openingDate, that.openingDate) &&
                Objects.equals(this.closingDate, that.closingDate) &&
                Objects.equals(this.periodStats, that.periodStats);
    }

    @Override
    public int hashCode() {
        return Objects.hash(account, openingDate, closingDate, periodStats);
    }

    @Override
    public String toString() {
        return "AccountDto[" +
                "account=" + account + ", " +
                "openingDate=" + openingDate + ", " +
                "closingDate=" + closingDate + ", " +
                "periodStats=" + periodStats + ']';
    }
}
