import * as React from "react";
import {Button, Container, Form, Header} from "semantic-ui-react";
import {UserContext} from "../../context/UserContext";

export const Login = () => {

    const {login} = React.useContext(UserContext);

    const [email, setEmail] = React.useState(null);
    const [password, setPassword] = React.useState(null);

    const handleSubmit = async () => {

        if (email === null || password === null) {

            alert("invalid login info");
            return;
        }

        const loginResponse = await login(email, password);
        if (loginResponse !== 204) {

            alert("an error occurred when logging in");
        }
    };

    return <Container>

        <Header as={"h2"}>Login</Header>

        <Form>
            <Form.Field>
                <label>Username</label>
                <input
                    placeholder='Email'
                    onChange={(event => setEmail(event.target.value))}/>
            </Form.Field>
            <Form.Field>
                <label>Password</label>
                <input
                    type={"password"}
                    onChange={(event => setPassword(event.target.value))}/>
            </Form.Field>
            <Button onClick={handleSubmit}>Submit</Button>
        </Form>
    </Container>;
};
