<script lang="ts">
    import {onMount} from "svelte";
    import {fetchOptions} from "$lib/beanvest/api.ts";
    import {Filters} from "$lib/beanvest/filters.ts";
    import type {ColumnDto} from "$lib/beanvest/apiTypes.d.ts";


    let filters: Filters;
    $: filters = new Filters([]);

    let columns: ColumnDto[] = [];
    onMount(() => {
        fetchOptions().then(options => {
            filters = new Filters(options.columnDtos);
            columns = options.columnDtos;
        });
    })

    function removeColumn(i) {
        filters.removeColumn(i);
        filters = filters;
    }

    function swap(i) {
        filters.swap(i);
        filters = filters;
    }

    function addColumn(colId) {
        filters.addColumn(colId);
        filters = filters;
    }

</script>

<div class="col-auto m-5">
    <form>
        <div class="col-auto">
            <label for="startDate" class="form-label">Start</label>
            <input type="text" id="startDate" class="form-control">
        </div>

        <div class="col-auto">
            <label for="endDate" class="form-label">End</label>
            <input type="text" id="endDate" class="form-control">
        </div>

        <div class="col-auto">
            <label for="endDate" class="form-label">Interval</label>
            <select class="form-select" aria-label="Report period">
                <option selected>none</option>
                <option value="MONTH">month</option>
                <option value="QUARTER">quarter</option>
                <option value="YEAR">year</option>
            </select>
        </div>

        <div class="form-check">
            <input class="form-control form-check-input" type="checkbox" value="" id="cumulative">
            <label class="form-check-label" for="cumulative">
                Cumulative
            </label>
        </div>

        <div class="col-auto">
            <label>Columns selected: </label>
            <div class="m-1">
                {#each filters.getSelectedColumns() as column, i}
                    <button type="button" class="btn btn-primary btn-warning"
                            on:click={() => removeColumn(i)}>{column.fullName}</button>
                    {#if filters.getSelectedColumns().length > i + 1}
                        <button type="button" class="btn btn-secondary btn-sm" on:click={() => swap(i)}>⇆</button>
                    {/if}
                {/each}
            </div>
        </div>

        <div class="m-3">
            <label>Columns available:</label>
            <div class="m-1">
                {#if columns.length === filters.getSelectedColumns().length}
                    none
                {:else}
                    {#each columns as column}
                        {#if !filters.isSelected(column.id)}
                            <button type="button" class="btn btn-secondary btn-sm"
                                    on:click={() => addColumn(column.id)}>{column.fullName}</button>
                        {/if}
                    {/each}
                {/if}
            </div>
        </div>
    </form>
</div>