import * as React from "react";
import {Container} from "semantic-ui-react";
import Header from "semantic-ui-react/dist/commonjs/elements/Header";

export const PageWrapper = props => <Container>

    <Header as={"h1"} dividing>Cinama page</Header>
    {props.children}
</Container>;