import * as React from "react";
import {Button, Container, Header} from "semantic-ui-react";
import {PaginationFetcher} from "../../../../utils/PaginationFetcher";
import {TimelineContext} from "../../../context/TimelineContext";

export const TimelineMessages = props => {


    const {setLocation, setBasePath, messagePage} = React.useContext(TimelineContext);

    React.useEffect(() => {

        if (props.email) {

            const base = "/users/" + props.email + "/timeline";
            setBasePath(base);
        }
    }, [props.email]);


    return <Container>

        {messagePage.list.map(message => <Container>
            <Header as={"h4"}>Posted: {new Date(message.creationTime).toString()}}</Header>
            {message.text}
        </Container>)}
        <Button onClick={() => {setLocation(messagePage.next)}}>Load</Button>
    </Container>
};