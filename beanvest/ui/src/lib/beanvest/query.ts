import type {PeriodInterval} from "$lib/imported/apiTypes.d.ts";
import {Filters, FiltersOptions} from "$lib/beanvest/filters";

class BeanvestQuery {
    public columns: string[];
    public startDate: string | null;
    public endDate: string | null;
    public interval: PeriodInterval;
    public cumulative: boolean;

    constructor(columns: string[], startDate: string | null, endDate: string | null, interval: PeriodInterval, cumulative: boolean) {
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

export function buildFilters(filterOptions: FiltersOptions, query: any | BeanvestQuery): Filters {
    let filters = Filters.createEmpty(filterOptions);
    query.columns.forEach((c) => filters.addColumn(c));
    filters.cumulative = query.cumulative;
    filters.setInterval(query.interval);
    filters.cumulative = query.cumulative;
    filters.startDate = query.startDate;
    filters.endDate = query.endDate;
    return filters;
}