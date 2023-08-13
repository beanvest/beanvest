<script>

    const columns = {
        "rgain": "Realized gain",
        "ugain": "Unrealized gain",
        "cost": "Cost",
        "value": "Value",
        "profit": "Profit",
        "xirr": "Xirr"
    }
    const columnGroups = [
        ["rgain", "ugain", "xirr"],
        ["cost", "value", "profit"]
    ]

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
        filters.selectedColumns[i] = filters.selectedColumns[i+1];
        filters.selectedColumns[i+1] = tmp;
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
                    <button type="button" class="btn btn-primary btn-warning" on:click={() => removeColumn(i)}>{columns[colId]}</button>
                    {#if filters.selectedColumns.length > i+1}
                        <button type="button" class="btn btn-secondary btn-sm" on:click={() => swap(i)}>⇆</button>
                    {/if}
                {/each}
            </div>
        </div>

        <div class="m-3">
            <label>Columns available: </label>

            {#each columnGroups as group}
                <div class="m-1">
                    {#each group as colId}
                        {#if !filters.selectedColumns.includes(colId)}
                            <button type="button" class="btn btn-secondary btn-sm" on:click={() => addColumn(colId)}>{columns[colId]}</button>
                        {/if}
                    {/each}
                </div>
            {/each}
        </div>
    </form>
</div>