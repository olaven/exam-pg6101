import * as React from "react";
import {UserContext} from "../../context/UserContext";
import {Button, Container, Form, Header} from "semantic-ui-react";

export const Signup = () => {

    const {signUp} = React.useContext(UserContext);

    const [username, setUsername] = React.useState(null);
    const [password, setPassword] = React.useState(null);
    const [repeatedPassword, setRepeatedPassword] = React.useState(null);

    const handleSubmit = async () => {

        if (password !== repeatedPassword || username === null || password === null || repeatedPassword === null) {

            alert("invalid values.");
            return;
        }

        const signUpStatus = await signUp(username, password);
        if (signUpStatus !== 204) {

            alert("error creating user.");
        }
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
            <Button onClick={handleSubmit}>Submit</Button>
        </Form>
    </Container>
};