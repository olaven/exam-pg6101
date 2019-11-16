import * as React from "react";
import {UserContext} from "../../context/UserContext";
import {Header, List, Segment} from "semantic-ui-react";

export const UserInfo = () => {

    const {user} = React.useContext(UserContext);

    return <Segment>
        <Header as={"h2"}>{user.name}</Header>
        <Header as={"h3"}>Roles</Header>
        <List items={user.roles}/>
    </Segment>
};