import {ColumnDto} from "$lib/beanvest/apiTypes.d.ts";

type ColumnDtoById = {
    [key: string]: ColumnDto;
}

export class Filters {
    _columns: ColumnDtoById;

    private startDate: string|null = null
    private endDate: string|null = null
    private readonly selectedColumns: string[]
    private interval: string
    private deltas: boolean


    constructor(columns: ColumnDto[]) {
        this.selectedColumns = [];
        this._columns = columns.reduce(function (map, obj) {
            map[obj.id] = obj;
            return map;
        }, {});
    }

    addColumn(colId) {
        if (!this._columns.hasOwnProperty(colId)) {
            throw new ReferenceError("Unknown column `" + colId + "`");
        }
        if (!this.selectedColumns.includes(colId)) {
            this.selectedColumns.push(colId)
        }
    }

    removeColumn(i) {
        this.selectedColumns.splice(i, 1)
    }

    swap(i) {
        let tmp = this.selectedColumns[i];
        this.selectedColumns[i] = this.selectedColumns[i + 1];
        this.selectedColumns[i + 1] = tmp;
    }

    getSelectedColumns(): ColumnDto[]
    {
        return this.selectedColumns.map(colId => this._columns[colId])
    }
}

