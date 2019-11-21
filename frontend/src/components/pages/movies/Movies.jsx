import * as React from "react";
import {Button, Container, Header} from "semantic-ui-react";
import {MovieCard} from "./MovieCard";
import {PaginationFetcher} from "../../../utils/PaginationFetcher";

export const Movies = () => {

    const [location, setLocation] = React.useState(null);
    const moviePage = PaginationFetcher(location, "/movies");

    return <Container>
        <Header>All Movies</Header>
        {moviePage.list.map(movie => <MovieCard movie={movie}/>)}
        <Button onClick={() => {setLocation(moviePage.next)}}>Load</Button>
    </Container>;
};