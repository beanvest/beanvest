<script lang="ts">
    import {onMount} from 'svelte';
    import {BarController, BarElement, CategoryScale, Chart, LinearScale, Title} from "chart.js";
    import type {PortfolioStatsDto2} from "$lib/imported/apiTypes";

    export var report: PortfolioStatsDto2;
    let chartId = "chart-" + Math.random();
    function init() {
        Chart.register(LinearScale)
        Chart.register(BarController)
        Chart.register(CategoryScale)
        Chart.register(BarElement)
        Chart.register(Title)
    }

    function chart() {
        if (report) {
            init();

            let datasets = []
            let backgroundColor = [
                'rgba(255, 99, 132, 0.2)',
                'rgba(255, 159, 64, 0.2)',
                'rgba(255, 205, 86, 0.2)',
                'rgba(75, 192, 192, 0.2)',
                'rgba(54, 162, 235, 0.2)',
                'rgba(153, 102, 255, 0.2)',
                'rgba(201, 203, 207, 0.2)'
            ];
            var stat = report.stats[0];

            for (let accountDto of report.accountDtos) {
                if (accountDto.account.includes(".*")) {
                    continue;
                }
                let bg = backgroundColor.pop();
                let values = []
                for (let period of report.periods) {
                    let value: number | null = accountDto.periodStats[period].stats[stat].value;
                    if (typeof value === "undefined") {
                        value = null;
                    }
                    values.push(value)
                }

                datasets.push({
                    label: stat,
                    data: values,
                    backgroundColor: bg,
                    borderWidth: 0,
                });
            }

            console.log(datasets);

            let data = {
                labels: report.periods,
                datasets: datasets
            };

            let htmlElement = document.getElementById(chartId);
            new Chart(htmlElement, {
                type: 'bar',
                data: data,
                options: {
                    scales: {
                        y: {
                            stacked: true,
                            beginAtZero: true,
                        },
                        x: {
                            stacked: true,
                        },
                    },
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        title: {
                            display: true,
                            text: stat,
                        }
                    }
                }
            });

        }
    }

    onMount(() => {
        chart();
    });
</script>

<div>
    {#if report}
        <div class="m-3 chart">
            <canvas id="{chartId}" class="chart"></canvas>
        </div>
    {/if}
</div>

<style>
    .chart {
        height: 300px;
    }
</style>
