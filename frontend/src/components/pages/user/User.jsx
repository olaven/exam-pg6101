import * as React from "react"
import {Container} from "semantic-ui-react";
import {UserContext} from "../../context/UserContext";
import {UserInfo} from "./UserInfo";
import {Signup} from "./Signup";
import {Login} from "./Login";
import {Logout} from "./Logout";

export const User = () => {

    const {auth} = React.useContext(UserContext);

    return <Container>

        {auth ?
            <>
                {UserInfo()}
                {Logout()}
            </> :
            <>
                {Login()}
                {Signup()}
            </>
        }
    </Container>;
};

