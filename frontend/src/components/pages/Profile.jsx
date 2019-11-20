import * as React from "react";
import {Container, Header} from "semantic-ui-react";
import {UserContext} from "../context/UserContext";

export const Profile = props => {

    const {auth, getUser} = React.useContext(UserContext);

    const [user, setUser] = React.useState(null);


    React.useEffect(() => { //TODO: auth is always null

        const loadUser = async () => {

            if (props.email) {

                const user = await getUser(props.email);
                setUser(user);
            } else if(auth !== null) {

                console.log("should try to get ", auth.name);
                const user = await getUser(auth.name);
                setUser(user);
            }
        };

        loadUser()
    }, [auth]);

    if (!user) return <Container>
        User is loading..
    </Container>;

    return <Container>

        <Header as={"h1"}>Profile of {user.givenName} {user.familyName}</Header>

        {user.email !== auth.name?
            <Header as={"h2"}>Create a new message</Header>:
            <Header as={"h2"}>This is your timeline</Header>
        }

        <Header as={"h2"}>Timeline</Header>
    </Container>;
}