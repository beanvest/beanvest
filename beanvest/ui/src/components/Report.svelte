<script lang="ts">
    import {onMount} from "svelte"
    import {PortfolioStatsDto2, StatsV2} from "$lib/apiTypes.d.ts"

    const apiURL = "http://localhost:5173/sample1.json"

    let report: PortfolioStatsDto2;
    $: report = null;

    async function fetchReport() {
        const response = await fetch(apiURL);

        report = await response.json();
    }

    onMount(() => {
        fetchReport();
    })
</script>

{#if report }
    <table>
        <tr>
            <td></td>
            {#each report.periods as period}
                <th colspan="{report.stats.length}">{period}</th>

            {/each}
        </tr>
        <tr>
            <th>accounts</th>
            {#each report.periods as period}
                {#each report.stats as stat}
                    <th>{stat}</th>
                {/each}
            {/each}

        </tr>
        {#each report.accountDtos as accountDto}
            <tr>
                <td>{accountDto.account}</td>
                {#each Object.entries(accountDto.periodStats) as [periodTitle, stats]}
                    {#each Object.entries(stats.stats) as [statName, value]}
                        <td class="value">{value.value.toFixed(0)}</td>
                    {/each}
                {/each}
            </tr>

        {/each}
    </table>
{/if}
{#if !report }
    Loading report...
{/if}

<style>
    table td {
        padding: 5px;
    }

    td.value {
        text-align: right;
    }
</style>