import * as React from "react";
import {BrowserRouter, Switch, Route} from "react-router-dom";
import {UserContextProvider} from "./context/UserContext"
import {Layout} from "./layout/Layout";
import {pages} from "./pages";


const renderRoutes = (page) => <Route exact path={page.path} render={props =>
      <page.component key={page.name} {...props}/>
    }
/>;

export const App = () => <BrowserRouter>
    <UserContextProvider>
        <Layout pages={pages}>
            <Switch>
                {pages.map(page => renderRoutes(page))}
            </Switch>
        </Layout>
    </UserContextProvider>
</BrowserRouter>;