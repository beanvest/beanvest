import {ColumnDto} from "$lib/beanvest/apiTypes.d.ts";

type ColumnDtoById = {
    [key: string]: ColumnDto;
}

export class Filters {
    _columns: ColumnDtoById;

    startDate: string|null = null;
    endDate: string|null = null
    selectedColumns: ColumnDto[] = []
    interval: string[] = []
    deltas: boolean


    constructor(columns: ColumnDto[]) {
        this._columns = columns.reduce(function (map, obj) {
            map[obj.id] = obj;
            return map;
        }, {});
    }

    addColumn(colId) {
        let column = this._columns[colId];
        this.selectedColumns.push(column)
    }

    removeColumn(i) {
        this.selectedColumns.splice(i, 1)
    }

    swap(i) {
        let tmp = this.selectedColumns[i];
        this.selectedColumns[i] = this.selectedColumns[i + 1];
        this.selectedColumns[i + 1] = tmp;
    }
}

