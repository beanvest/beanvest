<script>
	import 'bootstrap/dist/css/bootstrap.min.css';
</script>

<main class="d-flex flex-nowrap">
	<h1 class="visually-hidden">Sidebars examples</h1>

	<div class="d-flex flex-column flex-shrink-0 p-3 text-bg-dark" style="width: 280px;">
		<a
			href="/"
			class="d-flex align-items-center mb-3 mb-md-0 me-md-auto text-white text-decoration-none"
		>
			LOGO
			<span class="fs-4">Sidebar</span>
		</a>
		<hr />
		<ul class="nav nav-pills flex-column mb-auto">
			<li class="nav-item">
				<a href="/" class="nav-link text-white" aria-current="page"> Home </a>
			</li>
			<li>
				<a href="/filters" class="nav-link text-white"> Filters </a>
			</li>
			<li>
				<a href="/reports" class="nav-link text-white"> Reports </a>
			</li>
			<li>
				<a href="/charts" class="nav-link text-white"> Charts </a>
			</li>
		</ul>
	</div>

	<div class="b-example-divider b-example-vr" />

	<div class="w-100 p-3 bg-body-tertiary" style="width: 500px;">
		<slot />
	</div>
</main>

<style>
	div {
		border: 1px;
	}
</style>
