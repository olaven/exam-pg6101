import * as React from "react";
import {ApiFetch} from "../../ApiFetch";

export const MovieFetcher = (next) => {

    const [moviePages, setMoviePages] = React.useState({
        list: [], next: null
    });

    const doFetch = async () => {

        const response = await ApiFetch(next? next: "/movies");
        if (response.status === 200) {

            const page = (await response.json()).data;
            setMoviePages(page);
        } else {

            console.log("response failed: ", response);
        }
    };

    React.useEffect(() => {

        doFetch();
    }, [next]);

    return moviePages;
};