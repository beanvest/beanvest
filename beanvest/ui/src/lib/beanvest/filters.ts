import { ColumnDto, OptionsDto } from '$lib/imported/apiTypes.d.ts';
import type { PeriodInterval } from '$lib/imported/apiTypes.d.ts';

type ColumnDtoById = {
	[key: string]: ColumnDto;
};

export class Filters {
	private readonly _columns: ColumnDtoById;
	public legalOptions: OptionsDto;

	public startDate: string | null = null;
	public endDate: string | null = null;
	public cumulative: boolean;
	private readonly selectedColumns: string[];
	private interval: PeriodInterval;
	private deltas: boolean;

	public static createEmpty() {
		let optionsDto = new OptionsDto();
		optionsDto.columns = [];
		optionsDto.intervals = [];
		return new Filters(optionsDto);
	}

	private constructor(options: OptionsDto) {
		this.cumulative = false;
		this.selectedColumns = [];
		this._columns = options.columns.reduce(function (map, obj) {
			map[obj.id] = obj;
			return map;
		}, {});
		this.legalOptions = options;
	}

	public addColumn(colId) {
		if (!this._columns.hasOwnProperty(colId)) {
			throw new ReferenceError('Unknown column `' + colId + '`');
		}
		if (!this.selectedColumns.includes(colId)) {
			this.selectedColumns.push(colId);
		}
	}

	public removeColumn(i) {
		this.selectedColumns.splice(i, 1);
	}

	public swap(i) {
		let tmp = this.selectedColumns[i];
		this.selectedColumns[i] = this.selectedColumns[i + 1];
		this.selectedColumns[i + 1] = tmp;
	}

	public getSelectedColumns(): ColumnDto[] {
		return this.selectedColumns.map((colId) => this._columns[colId]);
	}

	public isSelected(colId: string): boolean {
		return this.selectedColumns.includes(colId);
	}

	setInterval(interval: PeriodInterval) {
		if (!this.legalOptions.intervals.includes(interval)) {
			throw new Error('unknown interval ' + interval);
		}
		this.interval = interval;
	}

	getInterval(): PeriodInterval {
		return this.interval;
	}

	getSelectedColumnsIds() {
		return this.selectedColumns;
	}
}
