import * as React from "react";
import {Button, Container, Header} from "semantic-ui-react";
import {Link} from "react-router-dom";
import {UserContext} from "../../context/UserContext";
import {Advertisements} from "./Advertisements";


export const Home = () => {

    const {auth, user} = React.useContext(UserContext);

    return <div>
        <Header as={"h1"}>Welcome to the Social Page!</Header>
        {user?
            <Header as={"h1"}>Welcome, {user.givenName}!</Header>:
            null
        }
        {auth?
            <Container>
                <Link to={"/profile/" + auth.name}>
                    <Button color={"green"}> GO TO PROFILE </Button>
                </Link>
                <Advertisements/>
            </Container>:
            <Link to={"/login"}>
                <Button color={"green"}> Login/signup </Button>
            </Link>
        }
    </div>
};