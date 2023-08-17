import {ColumnDto, OptionsDto} from "$lib/beanvest/apiTypes.d.ts";
import type {PeriodInterval} from "$lib/beanvest/apiTypes.d.ts";

type ColumnDtoById = {
    [key: string]: ColumnDto;
}

export class Filters {
    private _columns: ColumnDtoById;
    public legalOptions: OptionsDto;

    private startDate: string | null = null
    private endDate: string | null = null
    private readonly selectedColumns: string[]
    private interval: string
    private deltas: boolean

    public static createEmpty() {
        let optionsDto = new OptionsDto();
        optionsDto.columns = [];
        optionsDto.intervals = [];
        return new Filters(optionsDto)
    }

    private constructor(options: OptionsDto) {
        this.selectedColumns = [];
        this._columns = options.columns
            .reduce(function (map, obj) {
                map[obj.id] = obj;
                return map;
            }, {});
        this.legalOptions = options;
    }

    public addColumn(colId) {
        if (!this._columns.hasOwnProperty(colId)) {
            throw new ReferenceError("Unknown column `" + colId + "`");
        }
        if (!this.selectedColumns.includes(colId)) {
            this.selectedColumns.push(colId)
        }
    }

    public removeColumn(i) {
        this.selectedColumns.splice(i, 1)
    }

    public swap(i) {
        let tmp = this.selectedColumns[i];
        this.selectedColumns[i] = this.selectedColumns[i + 1];
        this.selectedColumns[i + 1] = tmp;
    }

    public getSelectedColumns(): ColumnDto[] {
        return this.selectedColumns.map(colId => this._columns[colId])
    }

    public isSelected(colId: string): boolean {
        return this.selectedColumns.includes(colId)
    }

    setInterval(interval: PeriodInterval) {
        if (!this.legalOptions.intervals.includes(interval)) {
            throw new Error("unknown interval " + interval)
        }
        this.interval = interval;
    }

    getInterval(): string {
        return this.interval;
    }
}

