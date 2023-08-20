/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 3.2.1263

export class PortfolioStatsDto2 {
    accounts: string[];
    periods: string[];
    stats: string[];
    accountDtos: AccountDto2[];
    userErrors: string[];
}

export class OptionsDto {
    columns: ColumnDto[];
    intervals: PeriodInterval[];
}

export class AccountDto2 {
    account: string;
    openingDate: Date;
    closingDate?: Date;
    periodStats: { [index: string]: StatsV2 };
}

export class ColumnDto {
    id: string;
    fullName: string;
}

export class StatsV2 {
    stats: { [index: string]: Result<number, UserErrors> };
    metadata: AccountMetadata;
}

export class AccountMetadata {
    firstActivity: Date;
    closingDate?: Date;
}

export class Result<VALUE, ERROR> {
    value: VALUE;
    error: ERROR;
}

export class UserErrors {
    errors: UserError[];
}

export class UserError {
    error: ErrorEnum;
    maybeMessage?: string;
}

export type PeriodInterval = "NONE" | "YEAR" | "QUARTER" | "MONTH";

export type ErrorEnum = "DISABLED_FOR_ACCOUNT_TYPE" | "PRICE_NEEDED" | "ACCOUNT_NOT_OPEN_YET" | "XIRR_CALCULATION_FAILURE" | "XIRR_PERIOD_TOO_SHORT" | "VALIDATION_ERROR" | "XIRR_NO_TRANSACTIONS" | "DELTA_NOT_AVAILABLE" | "DELTA_NOT_AVAILABLE_NO_VALUE_STATS" | "CALCULATION_DISABLED";
