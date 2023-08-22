<script lang="ts">
    import {onMount} from 'svelte';
    import {fetchOptions} from '$lib/beanvest/api';
    import {Filters, FiltersOptions} from '$lib/beanvest/filters';
    import {buildQuery} from "$lib/beanvest/query";
    import type {PeriodInterval} from "$lib/imported/apiTypes";

    let filters: Filters;
    $: filters = new Filters(FiltersOptions.createEmpty());

    onMount(() => {
        fetchOptions().then((newOptions) => {
            filters.updateOptions(new FiltersOptions(newOptions));
            filters = filters;
        });
    });

    function setStart(x: string) {
        filters.startDate = x;
        filters = filters;
    }

    function setEnd(x: string) {
        filters.endDate = x;
        filters = filters;
    }

    function setInterval(x: PeriodInterval) {
        filters.setInterval(x);
        filters = filters;
    }

    function setCumulative(x: boolean) {
        filters.cumulative = x;
        filters = filters;
    }

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

    $: query = null;

    function generateQuery() {
        query = buildQuery(filters);
    }
</script>

<div class="col-auto m-5">
    <form>
        <div class="col-auto">
            <label for="startDate" class="form-label">Start</label>
            <input type="text" id="startDate" class="form-control" on:change={ev => setStart(ev.target.value)}/>
        </div>

        <div class="col-auto">
            <label for="endDate" class="form-label">End</label>
            <input type="text" id="endDate" class="form-control" on:change={ev => setEnd(ev.target.value)}/>
        </div>

        <div class="col-auto">
            <label for="endDate" class="form-label">Interval</label>
            <select class="form-select" aria-label="Report period"  on:change={ev => setInterval(ev.target.value)}>
                {#each filters.getLegalOptions().intervals as interval}
                    <option value={interval}
                    >{interval[0].toUpperCase() + interval.substring(1).toLowerCase()}</option
                    >
                {/each}
            </select>
        </div>

        <div class="form-check">
            <input class="form-control form-check-input" type="checkbox" id="cumulative"
                   on:change={ev => setCumulative(ev.target.checked)}/>
            <label class="form-check-label" for="cumulative"> Cumulative </label>
        </div>

        <div class="col-auto">
            <label>Columns selected: </label>
            <div class="m-1">
                {#each filters.getSelectedColumns() as column, i}
                    <button type="button" class="btn btn-primary btn-warning" on:click={() => removeColumn(i)}
                    >{column.fullName}</button
                    >
                    {#if filters.getSelectedColumns().length > i + 1}
                        <button type="button" class="btn btn-secondary btn-sm" on:click={() => swap(i)}
                        >⇆
                        </button
                        >
                    {/if}
                {/each}
            </div>
        </div>

        <div class="m-3">
            <label>Columns available:</label>
            <div class="m-1">
                {#if filters.getLegalOptions().columns.length === filters.getSelectedColumns().length}
                    none
                {:else}
                    {#each filters.getLegalOptions().columns as column}
                        {#if !filters.isSelected(column.id)}
                            <button
                                    type="button"
                                    class="btn btn-secondary btn-sm"
                                    on:click={() => addColumn(column.id)}>{column.fullName}</button>
                        {/if}
                    {/each}
                {/if}
            </div>
        </div>
        <button
                type="button"
                class="btn"
                on:click={() => generateQuery()}>submit
        </button>
        {#if query}
            <div>
                <h3>Query to send:</h3>
                <pre>{JSON.stringify(query, null, 4)}</pre>
                {query}
            </div>

        {/if}
    </form>
</div>
