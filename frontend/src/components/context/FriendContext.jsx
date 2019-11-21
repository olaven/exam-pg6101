import * as React from "react";
import {ApiFetch} from "../../utils/ApiFetch";
import {UserContext} from "./UserContext";


export const FriendContext = React.createContext({});

export const FriendContextProvider = props => {

    const { auth } = React.useContext(UserContext);

    /**
     * Returns true if the current
     * logged in user is friends with
     * the given user. False if not.
     * @param friendEmail
     */
    const checkIfFriends = async (friendEmail) => {

        const path = "/users/" + auth.name + "/friends/" + friendEmail;
        const response = await ApiFetch(path);

        return response.status === 200;
    };

    /**
     * Returns true if a pending friend request
     * exists from given email, to other email
     */
    const hasPendingRequest = (fromEmail, toEmail) => {


    };

    return <FriendContext.Provider value={{checkIfFriends}}>
        {props.children}
    </FriendContext.Provider>
};