import * as React from "react"
import {Button, Container, Form, Header} from "semantic-ui-react";
import {UserContext} from "../../context/UserContext";
import {UserInfo} from "./UserInfo";
import {Signup} from "./Signup";
import {Login} from "./Login";
import {Logout} from "./Logout";

export const User = () => {

    const {user} = React.useContext(UserContext);
    console.log("user: ", user);
    return <Container>

        {user?
            <>
                {UserInfo()}
                {Logout()}
            </>:
            <>
                {Login()}
                {Signup()}
            </>
        }
    </Container>;
};

