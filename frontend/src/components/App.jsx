import * as React from "react";
import {BrowserRouter, Route, Switch} from "react-router-dom";
import {UserContextProvider} from "./context/UserContext"
import {Layout} from "./layout/Layout";
import {pages} from "../utils/pages";
import {FriendContextProvider} from "./context/FriendContext";


const renderRoutes = (page) => <Route exact path={page.path} render={props =>
    <page.component key={page.name} {...props}/>
}
/>;

export const App = () => <BrowserRouter>
    <UserContextProvider>
        <FriendContextProvider>
            <Layout pages={pages}>
                <Switch>
                    {pages.map(page => renderRoutes(page))}
                </Switch>
            </Layout>
        </FriendContextProvider>
    </UserContextProvider>
</BrowserRouter>;