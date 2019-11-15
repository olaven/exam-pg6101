import * as React from "react";
import {ApiFetch} from "../ApiFetch";


export const UserContext = React.createContext({});

export const UserContextProvider = props => {

    const [user, setUser] = React.useState(null);

    //NOTE: updating user on first render
    React.useEffect(() => {

        updateUser();
    }, []);

    //only used inside this context, not should not be exported
    const updateUser = async () => {

        const response = await ApiFetch("authentication/user");

        if (response.status === 200) {

            const wrappedResponse = await response.json();
            const user = wrappedResponse.data;
            setUser(user);
        } else {

            setUser(null);
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

        const response = await ApiFetch("authentication/login", {
            method: "POST",
            body: body,
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.status === 204) {

            await updateUser();
        }

        return response.status;
    };

    const logout = async () => {

        const response = await ApiFetch("authentication/logout", {
            method: "delete"
        });

        await updateUser();
        return response;
    };

    /**
     * Tries to sign up.
     * If creation was successful, `user` will be set
     * @param username
     * @param password
     * @returns status code of response
     */
    const signUp = async (username, password) => {

        const body = JSON.stringify({
            "userId": username, //TODO: change userId to username in backend
            "password": password
        });

        const response = await ApiFetch("authentication/signUp", {
            method: "post",
            body: body,
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.status === 204) {
            await updateUser();
        }

        return response.status;
    };

    return <UserContext.Provider value={{user, setUser, login,  logout, signUp}}>
        {props.children}
    </UserContext.Provider>
};