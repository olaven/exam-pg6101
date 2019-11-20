import * as React from "react";
import {Container} from "semantic-ui-react";
import Header from "semantic-ui-react/dist/commonjs/elements/Header";
import {Links} from "./Links";
import {UserContext} from "../context/UserContext";

export const Layout = props => {


    const {auth} = React.useContext(UserContext);
    return <Container>

        <Header as={"h1"} dividing>Social Page {auth ? "- Logged in as " + auth.name : ""}</Header>
        <Links pages={props.pages}/>

        {props.children}
    </Container>;
};