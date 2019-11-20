import * as React from "react";
import {UserContext} from "../../context/UserContext";
import {Button, Container, Form, Header} from "semantic-ui-react";

export const Signup = () => {

    const {signUp} = React.useContext(UserContext);

    const [givenName, setGivenName] = React.useState(null);
    const [familyName, setFamilyName] = React.useState(null);
    const [email, setEmail] = React.useState(null);
    const [password, setPassword] = React.useState(null);
    const [repeatedPassword, setRepeatedPassword] = React.useState(null);

    const handleSubmit = async () => {

        if (password !== repeatedPassword || givenName == null || familyName == null || email === null || password === null || repeatedPassword === null) {

            alert("invalid values.");
            return;
        }

        const signUpStatus = await signUp(email, givenName, familyName, password);
        if (signUpStatus !== 204) {

            alert("error creating user.");
        }
    };

    return <Container>

        <Header as={"h2"}>Create User</Header>

        <Form>
            <Form.Field>
                <label>Given name</label>
                <input
                    type={"text"}
                    placeholder={"Faily Name, e.g. \"John\""}
                    onChange={(event => setGivenName(event.target.value))}/>
            </Form.Field>
            <Form.Field>
                <label>Family name</label>
                <input
                    type={"text"}
                    placeholder={"Faily Name, e.g. \"Johnson\""}
                    onChange={(event => setFamilyName(event.target.value))}/>
            </Form.Field>
            <Form.Field>
                <label>Email</label>
                <input
                    placeholder='Email'
                    onChange={(event => setEmail(event.target.value))}/>
            </Form.Field>
            <Form.Field>
                <label>Password</label>
                <input
                    type={"password"}
                    placeholder={"password"}
                    onChange={(event => setPassword(event.target.value))}/>
            </Form.Field>
            <Form.Field>
                <label>Repeated password</label>
                <input
                    type={"password"}
                    placeholder={"repeat same password"}
                    onChange={(event => setRepeatedPassword(event.target.value))}/>
            </Form.Field>
            <Button onClick={handleSubmit}>Submit</Button>
        </Form>
    </Container>
};