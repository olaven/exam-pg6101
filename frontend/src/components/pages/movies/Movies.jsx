import * as React from "react";
import {Container, Header} from "semantic-ui-react";
import {MovieFetcher} from "./MovieFetcher";
import {MovieCard} from "./MovieCard";

export const Movies = () => {

    const movies = MovieFetcher();

    return <Container>
        <Header>All Movies</Header>
        {movies.map(movie => <MovieCard movie={movie}/>)}
    </Container>;
};