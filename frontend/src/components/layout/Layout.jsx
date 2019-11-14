import * as React from "react";
import {Container} from "semantic-ui-react";
import Header from "semantic-ui-react/dist/commonjs/elements/Header";
import {Links} from "./Links";

export const Layout = props => <Container>

    <Header as={"h1"} dividing>Cinama page</Header>
    <Links pages={props.pages}/>
    {props.children}
</Container>;