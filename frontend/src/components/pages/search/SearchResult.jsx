import * as React from "react";
import {Container, Header} from "semantic-ui-react";
import {Link} from "react-router-dom";

export const SearchResult = props => <Container>

    <Header as={"h3"} style={{borderTop: "solid black"}}>{props.user.givenName} {props.user.familyName}</Header>
    <Header as={"h4"}>{props.user.email}</Header>
    <Link to={"/profile/" + props.user.email}>To profile</Link>
</Container>;
