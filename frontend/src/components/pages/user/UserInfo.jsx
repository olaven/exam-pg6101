import * as React from "react";
import {UserContext} from "../../context/UserContext";
import {Button, Header, List, Segment} from "semantic-ui-react";
import {ApiFetch} from "../../../utils/ApiFetch";

export const UserInfo = () => {

    const {auth} = React.useContext(UserContext);

    return <Segment>
        <Header as={"h2"}>{auth.name}</Header>
        <Header as={"h3"}>Roles</Header>
        <List items={auth.roles}/>
    </Segment>
};