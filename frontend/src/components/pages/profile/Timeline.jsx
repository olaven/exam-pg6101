import * as React from "react";
import {Container, Header} from "semantic-ui-react";
import {FriendContext} from "../../context/FriendContext";
import {TimelineMessages} from "./TimelineMessages";
import {MessageCreator} from "./MessageCreator";

export const Timeline = props => {


    const { checkIfFriends } = React.useContext(FriendContext);

    const [hasAccess, setHasAccess] = React.useState(false);

    React.useEffect(() => {

        checkIfFriends(props.user.email).then(areFriends => {

            setHasAccess(areFriends)
        });
    }, []);

    return <Container>

        <Header as={"h3"}>Timeline</Header>
        {hasAccess?
            <Container>
                <MessageCreator receiver={props.user.email}/>
                <TimelineMessages email={props.user.email}/>
            </Container>:
            <Container>
                Become friends with this user to see their timeline.
            </Container>
        }

    </Container>
};