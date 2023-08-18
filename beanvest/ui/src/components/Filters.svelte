<script lang="ts">
	import { onMount } from 'svelte';
	import { fetchOptions } from '$lib/beanvest/api.ts';
	import { Filters } from '$lib/beanvest/filters.ts';

	let filters: Filters;
	$: filters = Filters.createEmpty();

	onMount(() => {
		fetchOptions().then((options) => {
			filters = new Filters(options);
		});
	});

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
</script>

<div class="col-auto m-5">
	<form>
		<div class="col-auto">
			<label for="startDate" class="form-label">Start</label>
			<input type="text" id="startDate" class="form-control" />
		</div>

		<div class="col-auto">
			<label for="endDate" class="form-label">End</label>
			<input type="text" id="endDate" class="form-control" />
		</div>

		<div class="col-auto">
			<label for="endDate" class="form-label">Interval</label>
			<select class="form-select" aria-label="Report period">
				{#each filters.legalOptions.intervals as interval}
					<option value={interval}
						>{interval[0].toUpperCase() + interval.substring(1).toLowerCase()}</option
					>
				{/each}
			</select>
		</div>

		<div class="form-check">
			<input class="form-control form-check-input" type="checkbox" value="" id="cumulative" />
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
							>⇆</button
						>
					{/if}
				{/each}
			</div>
		</div>

		<div class="m-3">
			<label>Columns available:</label>
			<div class="m-1">
				{#if filters.legalOptions.columns.length === filters.getSelectedColumns().length}
					none
				{:else}
					{#each filters.legalOptions.columns as column}
						{#if !filters.isSelected(column.id)}
							<button
								type="button"
								class="btn btn-secondary btn-sm"
								on:click={() => addColumn(column.id)}>{column.fullName}</button
							>
						{/if}
					{/each}
				{/if}
			</div>
		</div>
	</form>
</div>
