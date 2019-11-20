import * as React from "react";
import {Container, Header} from "semantic-ui-react";
import {FriendContext} from "../../context/FriendContext";
import {TimelineMessages} from "./TimelineMessages";
import {MessageCreator} from "./MessageCreator";
import {UserContext} from "../../context/UserContext";

export const Timeline = props => {


    const {auth} = React.useContext(UserContext);
    const {checkIfFriends} = React.useContext(FriendContext);

    const [canSee, setCanSee] = React.useState(false);
    const [canPost, setCanPost] = React.useState(false);

    React.useEffect(() => {

        if (canPost) {

            setCanSee(true);
        } else {

            checkIfFriends(props.user.email).then(areFriends => {

                setCanSee(areFriends)
            });
        }
    }, [auth]);

    React.useEffect(() => {

        const canPost = auth.name === props.user.email;
        setCanPost(canPost);
    }, [auth]);

    if (!canSee) {

        return <Header as={"h2"}>
            Become friends with this user to see their timeline.
        </Header>
    }

    return <Container>

        <Header as={"h3"}>Timeline</Header>
        {canPost?
            <MessageCreator receiver={props.user.email}/>: null
        }
        <TimelineMessages/>

    </Container>
};