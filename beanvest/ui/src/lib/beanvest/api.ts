import type {OptionsDto} from "$lib/beanvest/apiTypes";

const apiUrl = "http://localhost:5173/";
export async function fetchOptions() {
    const apiURL = apiUrl + "options.json";
    const response = await fetch(apiURL);
    let options: OptionsDto = await response.json();
    return options;
}