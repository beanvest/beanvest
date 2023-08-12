import { writable } from 'svelte/store';


let data = {};

export const report = writable(data);