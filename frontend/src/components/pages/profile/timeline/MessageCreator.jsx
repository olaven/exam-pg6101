import * as React from "react";
import {Button, Container, Form, TextArea} from "semantic-ui-react";
import {ApiFetch} from "../../../../utils/ApiFetch";
import {TimelineContext} from "../../../context/TimelineContext";

export const MessageCreator = props => {

    const { setLocation, triggerRefresh } = React.useContext(TimelineContext);

    const [text, setText] = React.useState("");

    const postMessage = async () => {

        if (text.length < 1) {

            alert("Please write more text.");
            return;
        }

        const message = JSON.stringify({
            text,
            receiverEmail: props.receiverEmail,
            senderEmail: props.receiverEmail,
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

            // will trigger a refresh of the messages
            setLocation(null);
            triggerRefresh()
        } else {

            console.error(response);
            alert("An error occured when posting message");
        }
    };

    return <Form>
        <TextArea
            onChange={(event) => {setText(event.target.value)}}
            placeholder={"What do you want to say?"}/>
        <Button onClick={postMessage}>Post and reload</Button>
    </Form>
};