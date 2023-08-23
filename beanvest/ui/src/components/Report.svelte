<script lang="ts">
    import {onMount} from 'svelte';
    import {PortfolioStatsDto2} from '$lib/imported/apiTypes.d.ts';
    import Chart from "./Chart.svelte";

    const apiURL = 'http://localhost:5173/sample%s.json';
    export let sampleNumber = 3;
    let report: PortfolioStatsDto2;
    $: report = null;

    async function fetchReport(num: number) {
        const response = await fetch(apiURL.replace("%s", num.toString()));

        report = await response.json();
        // console.log(Object.entries(report.accountDtos[0].periodStats[]);
    }

    onMount(() => {
        fetchReport(sampleNumber);
    });
</script>


<div>
    {#if report}
        <Chart report="{report}"/>
        <table class="table table-bordered">
            <tr>
                <td/>
                {#each report.periods as period}
                    <th colspan={report.stats.length}>{period}</th>
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
                    {#each report.periods as period}
                        {#each report.stats as stat}
                            <td class="value">{accountDto.periodStats[period].stats[stat].value.toFixed(0)}</td>
                        {/each}
                    {/each}
                </tr>
            {/each}
        </table>
    {/if}
    {#if !report}
        Loading report...
    {/if}
</div>

<style>
    td.value {
        text-align: right;
    }
    td, th {
        border-left: 1px solid lightgray;
        padding: 0 0.4em;
    }
</style>
