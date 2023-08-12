<script>
    const apiURL = "http://localhost:5173/sample1.json";

    $: report = null;

    async function getData() {
        const response = await fetch(apiURL);
        let newVar = await response.json();
        report = newVar;
    }

</script>

<div on:click={getData}>load report</div>
{#if report }
    {#each report.accounts as account}
        <div>{account}</div>
    {/each}
{/if}