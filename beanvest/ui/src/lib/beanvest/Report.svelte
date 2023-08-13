<script>
    const apiURL = "http://localhost:5173/sample1.json";

    /** @type {PortfolioStatsDto2} */
    report;

    $: report = null;

    async function fetchReport() {
        const response = await fetch(apiURL);

        report = await response.json();
    }


</script>

<div on:click={fetchReport}>load report</div>
{#if report }
    <table>
        <tr>
            <th>accounts</th>
            {#each report.periods as period}
                <th>{period.start}</th>
            {/each}
        </tr>
        {#each report.accountDtos as accountDto}
            <tr><td>{accountDto.account}</td>
                {#each Object.entries(accountDto.periodStats) as [periodTitle, stats]}
                    {#each Object.entries(stats.stats) as [statName, value]}
                    <td class="value">{value.value.toFixed(0)}</td>
                        {/each}
                {/each}
            </tr>

        {/each}
    </table>
{/if}

<style>
    td.value {
        text-align: right;
    }
</style>