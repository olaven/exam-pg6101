import * as React from "react";
import {Container, Header} from "semantic-ui-react";
import {PaginationFetcher} from "../../../utils/PaginationFetcher";

export const TimelineMessages = props => {

    const [basePath, setBasePath] = React.useState(null);
    const [location, setLocation] = React.useState(null);
    const messagePage = PaginationFetcher(location, basePath);

    console.log("props in child: ", props);
    React.useEffect(() => {

        if (props.email) {

            setBasePath("/users/" + props.email + "/timeline");
        }
    }, [props.email]);


    console.log(messagePage);
    return <Container>

        {messagePage.list.map(message => <Container>
            <Header as={"h4"}>Posted: {new Date(message.creationTime).toString()}}</Header>
            {message.text}
        </Container>)}
    </Container>
};