import * as React from "react";
import {UserContext} from "../../../context/UserContext";
import {PaginationFetcher} from "../../../../utils/PaginationFetcher";
import {Button, Container, Header} from "semantic-ui-react";

export const PendingRequests = props => {

    const {auth} = React.useContext(UserContext);
    const [location, setLocation] = React.useState(null);

    const base = "/requests?receiver=" + auth.name + "&status=PENDING";
    const requestsPage = PaginationFetcher(location, base);

    const onAccept = (senderEmail) => {

        //TODO: implement
    };

    const onReject = (senderEmail) => {

        //TODO: implement
    };

    return requestsPage.list.map(request =>
            <Container>
                <Header as={"h5"}>You have a request from {request.senderEmail}</Header>
                <Button onClick={() => {onAccept(request.senderEmail)}}>Accept</Button>
                <Button onClick={() => {onReject(request.senderEmail)}}>Reject</Button>
            </Container>
        )
};