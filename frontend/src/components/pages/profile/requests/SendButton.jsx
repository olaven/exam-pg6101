import * as React from "react";
import {UserContext} from "../../../context/UserContext";
import {Button} from "semantic-ui-react";
import {ApiFetch} from "../../../../utils/ApiFetch";

export const SendButton = props => {

   /*
   * have I sent? if not, button
   * */

   const { auth } = React.useContext(UserContext);

   const sendRequest = async () => {

        const requestDTO = JSON.stringify({
            receiverEmail: props.email,
            senderEmail: auth.name
        });

        const response = await ApiFetch("/requests", {
            method: "post",
            headers: {
                'Content-Type': 'application/json'
            },
            body: requestDTO
        });

        if (response.status === 201) {

            alert("Successful request was sent :-)");
        } else if (response.status === 409) {

            alert("You were not allowed to send tot his user. Does one already exist?");
        } else {

            console.log(response);
            alert("An error occured when sending request");
        }
   };

    return <Button
        onClick={sendRequest}>
        Send friend request to this user
   </Button>
};