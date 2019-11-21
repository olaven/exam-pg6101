import * as React from "react";
import {Container, Header} from "semantic-ui-react";
import {FriendContext} from "../../../context/FriendContext";
import {TimelineMessages} from "./TimelineMessages";
import {MessageCreator} from "./MessageCreator";
import {UserContext} from "../../../context/UserContext";

export const Timeline = props => {


    const {auth} = React.useContext(UserContext);
    const {checkIfFriends} = React.useContext(FriendContext);

    const [areFriends, setAreFriends] = React.useState(false);

    React.useEffect(() => {

        checkIfFriends(props.user.email).then(areFriends => {

            setAreFriends(areFriends)
        });
    }, []);

    if (auth && auth.name === props.user.email) {
        return <Container>
            <Header>Timeline</Header>
            <MessageCreator
                receiverEmail={props.user.email}
            />
            <TimelineMessages
                email={props.user.email}
            />
        </Container>
    } else if (areFriends) {


       return <Container>
           <Header>Timeline</Header>
           <TimelineMessages email={props.user.email}/>
       </Container>
    } else {

        return <Header as={"h2"}>
            Become friends with this user to see their timeline.
        </Header>
    }
};