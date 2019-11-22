import * as React from "react";
import Button from "semantic-ui-react/dist/commonjs/elements/Button";
import {UserContext} from "../../context/UserContext";

export const Logout = () => {

    const {logout} = React.useContext(UserContext);

    const handleLogout = async () => {

        const logoutStatus = await logout();
        console.log("logout status", logoutStatus);
    };

    return <Button onClick={handleLogout}>Logout</Button>
};