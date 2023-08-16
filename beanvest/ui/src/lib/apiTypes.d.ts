/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 3.2.1263

class PortfolioStatsDto2 {
    accounts: string[];
    periods: string[];
    stats: string[];
    accountDtos: AccountDto2[];
}

class AccountDto2 {
    account: string;
    openingDate: Date;
    closingDate?: Date;
    periodStats: { [index: string]: StatsV2 };
}

class StatsV2 {
    errors: string[];
    stats: { [index: string]: Result<number, UserErrors> };
    metadata: AccountMetadata;
}

class AccountMetadata {
    firstActivity: Date;
    closingDate?: Date;
}

class Result<VALUE, ERROR> {
    value: VALUE;
    error: ERROR;
}

class UserErrors {
    errors: UserError[];
}

class UserError {
    error: ErrorEnum;
    maybeMessage?: string;
}

type ErrorEnum = "DISABLED_FOR_ACCOUNT_TYPE" | "PRICE_NEEDED" | "ACCOUNT_NOT_OPEN_YET" | "XIRR_CALCULATION_FAILURE" | "XIRR_PERIOD_TOO_SHORT" | "VALIDATION_ERROR" | "XIRR_NO_TRANSACTIONS" | "DELTA_NOT_AVAILABLE" | "DELTA_NOT_AVAILABLE_NO_VALUE_STATS" | "CALCULATION_DISABLED";
