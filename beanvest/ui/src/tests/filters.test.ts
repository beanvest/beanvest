import {expect, it, describe} from 'vitest';
import {Filters} from "$lib/beanvest/filters.ts";
import {ColumnDto} from "$lib/beanvest/apiTypes.d.ts";

describe('filters', () => {
    it('has no selected columns by default', () => {
        let filters = getFilters();
        expect(filters.getSelectedColumns()).to.length(0)
    })
    it('adds columns', () => {
        let filters = getFilters();
        filters.addColumn("xirr")
        filters.addColumn("pro")
        let actual = filters.getSelectedColumns().map(c => c.id);
        expect(actual).to.contain("xirr")
        expect(actual).to.contain("pro")
    })
    it('cant add same column twice', () => {
        let filters = getFilters();
        filters.addColumn("pro")
        filters.addColumn("pro")
        let actual = filters.getSelectedColumns().map(c => c.id);
        expect(actual).to.length(1)
    })
});

function getFilters() {
    return new Filters([
        columnDto("xirr", "Xirr"),
        columnDto("pro", "Profit")
    ]);
}

function columnDto(id: string, fullName: string) {
    var dto = new ColumnDto();
    dto.id = id;
    dto.fullName = fullName;
    return dto;
}
