import * as React from "react";
import {Button, Container, Form, TextArea} from "semantic-ui-react";
import {ApiFetch} from "../../../utils/ApiFetch";

export const MessageCreator = props => {

    const {receiverEmail} = props;

    const [text, setText] = React.useState("");

    const postMessage = async () => {

        if (text.length < 1) {

            alert("Please write more text.");
            return;
        }

        const message = JSON.stringify({
            text,
            receiverEmail,
            senderEmail: receiverEmail,
            creationTime: Date.now()
        });


        const response = await ApiFetch("/messages", {
            method: "post",
            headers: {
                'Content-Type': 'application/json'
            },
            body: message
        });

        if (response.status === 201) {

            //TODO: update displayed messages
        } else {

            console.log(response);
            alert("An error occured when posting message");
        }
    };

    return <Form>
        <TextArea
            onChange={(event) => {setText(event.target.value)}}
            placeholder={"What do you want to say?"}/>
        <Button onClick={postMessage}>Post message</Button>
    </Form>
};