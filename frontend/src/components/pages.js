import {Home} from "./pages/Home";
import {Movies} from "./pages/Movies";
import {Screenings} from "./pages/Screenings";
import {Booking} from "./pages/Booking";
import {User} from "./pages/User";
import {Admin} from "./pages/Admin";

/**
 * A page should contain
 * .path, .name and .component
 * */
export const pages = [
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
        name: "User Page",
        path: "/user",
        component: User
    },
    {
        name: "Admin",
        path: "/admin",
        component: Admin
    }
];