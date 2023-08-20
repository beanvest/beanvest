import {expect, it, describe} from 'vitest';
import {Filters} from '$lib/beanvest/filters.ts';
import {ColumnDto, OptionsDto, PeriodInterval} from '$lib/imported/apiTypes.d.ts';
import {buildQuery} from "$lib/beanvest/query";

describe('filters', () => {
    describe('columns', () => {
        it('has no selected columns by default', () => {
            let filters = createFilters();

            expect(filters.getSelectedColumns()).to.length(0);
        });
        it('adds columns', () => {
            let filters = createFilters();
            filters.addColumn('xirr');
            filters.addColumn('pro');

            let actual = filters.getSelectedColumns().map((c) => c.id);

            expect(actual).to.contain('xirr');
            expect(actual).to.contain('pro');
        });
        it('cant add same column twice', () => {
            let filters = createFilters();
            filters.addColumn('pro');
            filters.addColumn('pro');

            let actual = filters.getSelectedColumns();

            expect(actual).to.length(1);
        });
        it('can check if selected by id', () => {
            let filters = createFilters();
            expect(filters.isSelected('pro')).false;

            filters.addColumn('pro');
            expect(filters.isSelected('pro')).true;
        });
        it('cant add unknown column', () => {
            let filters = createFilters();

            expect(() => filters.addColumn('asjdhkasjd')).to.throw();
        });
    });

    describe('interval', () => {
        it('can set interval', () => {
            let filters = createFilters();
            expect(filters.isSelected('none')).false;

            filters.setInterval('MONTH');
            expect(filters.getInterval()).eq('MONTH');
        });
        it('cant set wrong interval', () => {
            let filters = createFilters();

            expect(() => filters.setInterval('laskdj')).to.throw();
        });

        it('can be constructed without data', () => {
            Filters.createEmpty();
        });
    });

    describe('builds beanvest query', () => {
        it('can build query out of filters', () => {
            let filters = createFilters();
            filters.addColumn("xirr");
            filters.setInterval("MONTH");
            filters.endDate = "2023-01-01";
            filters.startDate = "2022-01-01";
            filters.cumulative = true;

            expect(buildQuery(filters)).to.eql({
                columns: ["xirr"],
                interval: "MONTH",
                endDate: "2023-01-01",
                startDate: "2022-01-01",
                cumulative: true,
            });
        });
    });
});

function createFilters() {
    let columns = [columnDto('xirr', 'Xirr'), columnDto('pro', 'Profit')];
    let optionsDto = new OptionsDto();
    optionsDto.columns = columns;
    optionsDto.intervals = ['MONTH', 'QUARTER', 'NONE'];

    return new Filters(optionsDto);
}

function columnDto(id: string, fullName: string) {
    var dto = new ColumnDto();
    dto.id = id;
    dto.fullName = fullName;
    return dto;
}
