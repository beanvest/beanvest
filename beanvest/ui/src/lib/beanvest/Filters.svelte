<script>
    $: filters = null;

    filters = {
        startDate: null,
        endDate: null,
        selectedColumns: [],
        interval: [],
        deltas: [],
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
            <label>Selected: </label>
                <button type="button" class="btn btn-secondary btn-warning">Xirr</button>
                <button type="button" class="btn btn-secondary btn-sm">⭾</button>
                <button type="button" class="btn btn-secondary btn-warning">Profit</button>
        </div>
        <div class="col-auto">
            <label>Available: </label>
            <div class="btn-group btn-group-sm" role="group" aria-label="Basic example">
                <button type="button" class="btn btn-secondary">Xirr</button>
            </div>
            <div class="btn-group btn-group-sm" role="group" aria-label="Basic example">
                <button type="button" class="btn btn-secondary">Profit</button>
            </div>
        </div>
    </form>
</div>