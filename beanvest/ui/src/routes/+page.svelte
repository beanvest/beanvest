<script>
    import 'bootstrap/dist/css/bootstrap.min.css';
    import Report from "$lib/beanvest/Report.svelte";
    import Filters from "$lib/beanvest/Filters.svelte";

    let filters = {} //TODO get it from the Filters module
</script>

<div class="container my-5">
    <h3>thats main page</h3>
    <Filters></Filters>
    <Report></Report>

    <h2>here are your filters:</h2>
    {#if filters !== null && filters !== undefined}
        {#each Object.entries(filters) as [filter, value]}
            <div>{filter}</div>
        {/each}
    {/if}
</div>
