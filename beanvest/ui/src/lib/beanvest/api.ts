import type {OptionsDto} from "$lib/beanvest/apiTypes";

export async function fetchOptions() {
    const apiURL = "/options.json";
    const response = await fetch(apiURL);
    let options: OptionsDto = await response.json();
    return options;
}