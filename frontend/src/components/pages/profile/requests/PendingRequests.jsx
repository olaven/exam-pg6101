import * as React from "react";
import {UserContext} from "../../../context/UserContext";
import {PaginationFetcher} from "../../../../utils/PaginationFetcher";
import {Button, Container, Header} from "semantic-ui-react";
import {ApiFetch} from "../../../../utils/ApiFetch";

export const PendingRequests = props => {

    const {auth} = React.useContext(UserContext);
    const [location, setLocation] = React.useState(null);
    const [trigger, setUpdateTrigger] = React.useState(0);

    const base = "/requests?receiver=" + auth.name + "&status=PENDING";
    const requestsPage = PaginationFetcher(location, base, trigger);

    const updateRequest = async (request, status) => {

        request.status = status;
        const response = await ApiFetch("/requests/" + request.id, {
            method: "put",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(request)
        });

        if (response.status === 204) {

            setLocation(null);
            const updatedTrigger = trigger + 1;
            setUpdateTrigger(updatedTrigger)
        } else {

            console.error("response: ", response);
            alert("An error occured when answering request");
        }
    } ;

    return <Container>
        <Header>Your pending friend requests: </Header>
        {requestsPage.list.map(request =>
            <Container key={request.id}>
                <Header as={"h5"}>You have a request from {request.senderEmail}</Header>
                <Button onClick={() => {updateRequest(request, "ACCEPTED")}}>Accept</Button>
                <Button onClick={() => {updateRequest(request, "REJECTED")}}>Reject</Button>
            </Container>
        )}
        <Button onClick={() => {setLocation(requestsPage.next)}}>Load</Button>
    </Container>
};