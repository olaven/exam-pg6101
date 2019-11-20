import * as React from "react";
import {Container, Header} from "semantic-ui-react";

export const SearchResult = props => <Container>

    <Header as={"h3"} style={{borderTop: "solid black"}}>{props.user.givenName} {props.user.familyName}</Header>
    <Header as={"h4"}>{props.user.email}</Header>
    TODO: PROFILE LINK
</Container>;
