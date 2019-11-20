import * as React from "react";
import {Container} from "semantic-ui-react";
import Header from "semantic-ui-react/dist/commonjs/elements/Header";
import {Links} from "./Links";
import {UserContext} from "../context/UserContext";

export const Layout = props => {


    const {user} = React.useContext(UserContext);
    console.log("user in layout", user);
    return <Container>

        <Header as={"h1"} dividing>Social Page {user ? "- Logged in as " + user.name : ""}</Header>
        <Links pages={props.pages}/>

        {props.children}
    </Container>;
};