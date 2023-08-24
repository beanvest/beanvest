/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 3.2.1263

export class PortfolioStatsDto2 {
    accounts: { [index: string]: AccountDetailsDto };
    periods: string[];
    stats: string[];
    accountDtos: AccountDto2[];
    userErrors: string[];
}

export class OptionsDto {
    columns: ColumnDto[];
    intervals: PeriodInterval[];
}

export class AccountDetailsDto {
    name: string;
    id: string;
    level: number;
    type: EntityType;
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
    stats: { [index: string]: Result<number, StatErrors> };
}

export class Result<VALUE, ERROR> {
    value: VALUE;
    error: ERROR;
}

export class StatErrors {
    errors: StatError[];
}

export class StatError {
    error: StatErrorEnum;
    maybeMessage?: string;
}

export type PeriodInterval = "NONE" | "YEAR" | "QUARTER" | "MONTH";

export type EntityType = "GROUP" | "ACCOUNT" | "HOLDING";

export type StatErrorEnum = "PRICE_NEEDED" | "ACCOUNT_NOT_OPEN_YET" | "XIRR_CALCULATION_FAILURE" | "VALIDATION_ERROR" | "NO_DATA_YET";
