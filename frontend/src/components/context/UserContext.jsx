import * as React from "react";
import {ApiFetch} from "../ApiFetch";


export const UserContext = React.createContext({});

export const UserContextProvider = props => {

    const [user, setUser] = React.useState(null);

    //only used inside this context, not should not be exported
    const updateUser = async () => {

        const response = await ApiFetch("authentication/user");
        if (response.status === 200) {

            const user = await response.json();
            setUser(user);
        } else {

            throw "An error has occured -> user was not logged in when calling /user";
        }
    };

    const logout = () => {

        console.log("TODO: LOGOUT FUNCTION")
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

        await updateUser();

        return response.status;
    };

    return <UserContext.Provider value={{user, setUser, logout, signUp}}>
        {props.children}
    </UserContext.Provider>
};