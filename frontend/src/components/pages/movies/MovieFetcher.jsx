import * as React from "react";
import {ApiFetch} from "../../ApiFetch";

export const MovieFetcher = () => {

    const [movies, setMovies] = React.useState([]);

    const doFetch = async () => {

        const response = await ApiFetch("movies");
        if (response.status === 200) {

            const movies = (await response.json()).data.list;
            console.log(movies); //TODO: take pagination into account
            setMovies(movies);
        }
    };

    React.useEffect(() => {

        doFetch();
    }, []);

    return movies;
};