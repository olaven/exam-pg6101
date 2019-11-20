import * as React from "react";
import {UserContext} from "../../context/UserContext";
import {Button, Header, List, Segment} from "semantic-ui-react";
import {ApiFetch} from "../../ApiFetch";

export const UserInfo = () => {

    const {auth} = React.useContext(UserContext);

    const postUserDetails = async () => {

        const userDTO = JSON.stringify({
            email: auth.name, givenName: "Test Given Name", familyName: "Test Family Name"
        });

        console.log("going to poset user: ", userDTO)

        const userResponse = await ApiFetch("/users", {
            method: "post",
            body: userDTO
        });

        console.log("Entier response: ", userResponse);
        console.log("Response : ", userResponse.status);
    };

    return <Segment>
        <Header as={"h2"}>{auth.name}</Header>
        <Header as={"h3"}>Roles</Header>
        <List items={auth.roles}/>

        <Button onClick={postUserDetails}>Post User Deatils</Button>
    </Segment>
};