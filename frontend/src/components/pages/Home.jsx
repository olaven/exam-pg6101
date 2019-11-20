import * as React from "react";
import {Header} from "semantic-ui-react";
import {Link} from "react-router-dom";
import {UserContext} from "../context/UserContext";


export const Home = () => {

    const {auth} = React.useContext(UserContext);

    return <div>
        <Header as={"h1"}>Welcomee to the Social Page!</Header>
        {auth?
            <Link to={"/profile/" + auth.name}>To profile</Link>:
            <Link to={"/login"}>Login or sign up</Link>
        }
    </div>
};