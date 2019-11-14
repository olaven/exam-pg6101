import * as React from "react";


export const UserContext = React.createContext({});

export const UserContextProvider = props => {

    const [user, setUser] = React.useState(null);

    const logout = () => {

        console.log("TODO: LOGOUT FUNCTION")
    };

    return <UserContext.Provider value={{user, setUser, logout}}>
        {props.children}
    </UserContext.Provider>
};