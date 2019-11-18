import * as React from "react";
import {Button, Container, Header} from "semantic-ui-react";
import {MovieFetcher} from "./MovieFetcher";
import {MovieCard} from "./MovieCard";

export const Movies = () => {

    const [location, setLocation] = React.useState(null);
    const moviePage = MovieFetcher(location);

    return <Container>
        <Header>All Movies</Header>
        {moviePage.list.map(movie => <MovieCard movie={movie}/>)}
        <Button onClick={() => {setLocation(moviePage.next)}}>Load more</Button>
    </Container>;
};