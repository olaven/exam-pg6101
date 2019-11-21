import * as React from "react";
import {ApiFetch} from "../../utils/ApiFetch";


export const UserContext = React.createContext({});

export const UserContextProvider = props => {

    const [auth, setAuth] = React.useState(null);
    const [user, setUser] = React.useState(null);

    //NOTE: updating user on first render
    React.useEffect(() => {

        updateAuth();
    }, []);

    /*
        The idea here is to automatically update the
        user info when the auth user changes
    */
    React.useEffect(() => {

        updateUser();
    }, [auth]);


    //only used inside this context, not should not be exported
    const updateAuth = async () => {

        const response = await ApiFetch("/authentication/user");

        if (response.status === 200) {

            const wrappedResponse = await response.json();
            const auth = wrappedResponse.data;
            setAuth(auth);

        } else {

            setAuth(null);
        }
    };


    const updateUser = async () => {

        // if not authenticated, means that user is logged out.
        if (auth) {

            const response = await ApiFetch("/users/" + auth.name);
            if (response.status === 200) {

                const wrappedResponse = await response.json();
                const user = wrappedResponse.data;
                setUser(user);
            }
        } else {

            setUser(null)
        }
    };


    /**
     * Tries to login.
     * Updates user if successful.
     * @param username
     * @param password
     * @returns {Promise<number>}
     */
    const login = async (username, password) => {

        const body = JSON.stringify({
            userId: username,
            password: password
        });

        const response = await ApiFetch("/authentication/login", {
            method: "POST",
            body: body,
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.status === 204) {

            await updateAuth();
        }

        return response.status;
    };

    const logout = async () => {

        const response = await ApiFetch("/authentication/logout", {
            method: "delete"
        });

        await updateAuth();
        return response;
    };


    const signUp = async (email, givenName, familyName, password) => {

        const authDTO = JSON.stringify({
            "userId": email,
            "password": password
        });

        const authResponse = await ApiFetch("/authentication/signUp", {
            method: "post",
            body: authDTO,
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (authResponse.status === 204) {

            const userDTO = JSON.stringify({
                email, givenName, familyName
            });

            const userResponse = await ApiFetch("/users", {
                method: "post",
                body: userDTO
            });

            console.log("creating user data: ", userResponse.status);

            await updateAuth();
        }

        return authResponse.status;
    };

    const getUser = async (email) => {

        const response = await ApiFetch("/users/" + email);
        if (response.status !== 200) return null;

        const wrappedResponse = await response.json();
        return wrappedResponse.data;
    };

    return <UserContext.Provider value={{auth, setAuth, user, setUser, login, logout, signUp, getUser}}>
        {props.children}
    </UserContext.Provider>
};