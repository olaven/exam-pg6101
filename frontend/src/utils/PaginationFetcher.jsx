import * as React from "react";
import {ApiFetch} from "./ApiFetch";


export const PaginationFetcher = (next, basePath, trigger) => {

    const [pages, setPages] = React.useState({
        list: [], next: null
    });

    const doFetch = async () => {

        const response = await ApiFetch(next? next: basePath);
        if (response.status === 200) {

            const page = (await response.json()).data;
            setPages(page);
        } else {

            console.log("response failed: ", response);
        }
    };

    React.useEffect(() => {

        doFetch()
    }, [trigger]);

    React.useEffect(() => {

        if (basePath) {

            doFetch();
        }
    }, [next, basePath]);

    return pages;
};