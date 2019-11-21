import * as React from "react";
import {Button, Container, Header, Input} from "semantic-ui-react";
import {PaginationFetcher} from "../../../utils/PaginationFetcher";
import {SearchResult} from "./SearchResult";

export const Search = () => {

    const [searchTerm, setSearchTerm] = React.useState(null);
    const [location, setLocation] = React.useState(null);
    const userPage = PaginationFetcher(location, "/users");

    React.useEffect(() => {

        if (searchTerm === null || searchTerm === "") {

            setLocation(null);
        } else {

            setLocation("/users?searchTerm=" + searchTerm)
        }
    }, [searchTerm]);

    return <Container>
        <Header as={"h2"}>Search for friends:</Header>

        <Input placeholder={"email"} onChange={(event) => {
            setSearchTerm(event.target.value)
        }}/>

        {userPage.list.map(user =>
            <SearchResult user={user} key={user.email}/>)
        }

        <Button onClick={() => {
            setLocation(userPage.next)
        }}>Load</Button>
    </Container>;
};