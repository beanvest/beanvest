import type {PeriodInterval} from "$lib/imported/apiTypes.d.ts";
import {Filters} from "$lib/beanvest/filters";

class BeanvestQuery {
    private columns: string[];
    private startDate: string | null;
    private endDate: string | null;
    private interval: PeriodInterval | null;
    private cumulative: boolean;

    constructor(columns: string[], startDate: string | null, endDate: string | null, interval: PeriodInterval | null, cumulative: boolean) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.columns = columns;
        this.interval = interval;
        this.cumulative = cumulative;
    }
}

export function buildQuery(filters: Filters): object {
    let beanvestQuery = new BeanvestQuery(
        filters.getSelectedColumnsIds(),
        filters.startDate,
        filters.endDate,
        filters.getInterval(),
        filters.cumulative);

    var a = {};
    Object.assign(a, beanvestQuery);
    return a;
}