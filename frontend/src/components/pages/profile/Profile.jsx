import * as React from "react";
import {Container, Header} from "semantic-ui-react";
import {UserContext} from "../../context/UserContext";
import {Timeline} from "./Timeline";

export const Profile = props => {

    const {auth, getUser} = React.useContext(UserContext);
    const [user, setUser] = React.useState(null);


    React.useEffect(() => {

        const loadUser = async () => {

            const { email } = props.match.params;
            const user = await getUser(email);
            setUser(user);
        };

        loadUser()
    }, [auth]);

    if (!user) return <Container>
        User is loading..
    </Container>;

    return <Container>

        <Header as={"h1"}>Profile of {user.givenName} {user.familyName}</Header>

        {auth && user.email !== auth.name?
            <Header as={"h2"}>Create a new message</Header>:
            <Header as={"h2"}>This is your timeline</Header>
        }
        <Timeline user={user}/>
    </Container>;
};