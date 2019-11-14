import * as React from "react";
import {
    BrowserRouter,
    Switch,
    Route
} from "react-router-dom";
import {Layout} from "./layout/Layout";
import {Booking} from "./pages/Booking";
import {Home} from "./pages/Home";
import {Movies} from "./pages/Movies";
import {Screenings} from "./pages/Screenings";
import {Admin} from "./pages/Admin";
import {NotFound} from "./pages/NotFound";


/**
 * A page should contain
 * .path, .name and .component
 * */
const pages = [
    {
        name: "Home",
        path: "/",
        component: Home
    },
    {
        name: "Movies",
        path: "/movies",
        component: Movies
    },
    {
        name: "Screenings",
        path: "/screenings",
        component: Screenings
    },
    {
        name: "Booking",
        path: "/booking",
        component: Booking
    },
    {
        name: "Admin",
        path: "/admin",
        component: Admin
    }
];

const renderRoutes = (page) => {

    return <Route exact path={page.path}
                  render={props =>
                      <page.component {...props}
                                 /*user={this.state.user}
                                 updateLoggedInUser={this.updateLoggedInUser}*/
                      />
                  }
    />
};

export const App = () => <BrowserRouter>
    <Layout pages={pages}>
        <Switch>
            {pages.map(page => renderRoutes(page))}
            <Route component={NotFound} />
        </Switch>
    </Layout>
</BrowserRouter>;