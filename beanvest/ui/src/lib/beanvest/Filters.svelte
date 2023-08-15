<script>
    import {onMount} from "svelte";

    const apiURL = "http://localhost:5173/options.json";

    $: columns = []

    async function fetchOptions() {
        const response = await fetch(apiURL);
        let body = await response.json();
        columns = body.columns.map(c => c.id);
    }

    onMount(() => {
        fetchOptions();
    })

    $: filters = {
        startDate: null,
        endDate: null,
        selectedColumns: ["cost", "value", "xirr"],
        interval: [],
        deltas: [],
    }

    function addColumn(colId) {
        filters.selectedColumns.push(colId)
        filters = filters;
    }

    function removeColumn(i) {
        filters.selectedColumns.splice(i, 1)
        filters = filters;
    }

    function swap(i) {
        let tmp = filters.selectedColumns[i];
        filters.selectedColumns[i] = filters.selectedColumns[i + 1];
        filters.selectedColumns[i + 1] = tmp;
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
            <input class="form-check-input" type="checkbox" value="" id="flexCheckDefault">
            <label class="form-check-label" for="flexCheckDefault">
                Cumulative
            </label>
        </div>

        <div class="col-auto">
            <label>Columns selected: </label>
            <div class="m-1">
                {#each filters.selectedColumns as colId, i}
                    <button type="button" class="btn btn-primary btn-warning"
                            on:click={() => removeColumn(i)}>{colId}</button>
                    {#if filters.selectedColumns.length > i + 1}
                        <button type="button" class="btn btn-secondary btn-sm" on:click={() => swap(i)}>⇆</button>
                    {/if}
                {/each}
            </div>
        </div>

        <div class="m-3">
            <label>Columns available:</label>
            <div class="m-1">
                {#each columns as colId}
                    {#if !filters.selectedColumns.includes(colId)}
                        <button type="button" class="btn btn-secondary btn-sm"
                                on:click={() => addColumn(colId)}>{colId}</button>
                    {/if}
                {/each}
            </div>
        </div>
    </form>
</div>