import * as React from "react"
import {Button, Checkbox, Container, Form, Header} from "semantic-ui-react";
import {ApiFetch} from "../ApiFetch"

export const User = () => <Container>
    {renderCreateUser()}
</Container>;


const renderLogin = () => {};
const renderUserInfo = () => {};

const renderCreateUser = () => {

    const [username, setUsername] = React.useState(null);
    const [password, setPassword] = React.useState(null);
    const [repeatedPassword, setRepeatedPassword] = React.useState(null);

    const createUser = async () => {

        if (password !== repeatedPassword || username === null || password === null || repeatedPassword === null) {

            alert("invalid values.");
            return;
        }

        const response = await ApiFetch("authentication/signUp", {
            method: "post",
            body: JSON.stringify({
                "userId": username, //TODO: change userId to username in backend
                "password": password
            }),
            headers: {
                'Content-Type': 'application/json'
            }
        });

        console.log(response);
    };

    return <Container>

        <Header as={"h2"}>Create User</Header>

        <Form>
            <Form.Field>
                <label>Username</label>
                <input
                    placeholder='Username'
                    onChange={(event => setUsername(event.target.value))}/>
            </Form.Field>
            <Form.Field>
                <label>Password</label>
                <input
                    type={"password"}
                    onChange={(event => setPassword(event.target.value))}/>
            </Form.Field>
            <Form.Field>
                <label>Repeated password</label>
                <input
                    type={"password"}
                    onChange={(event => setRepeatedPassword(event.target.value))}/>
            </Form.Field>
            <Button onClick={createUser}>Submit</Button>
        </Form>
    </Container>
};