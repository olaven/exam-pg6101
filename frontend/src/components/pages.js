import {Home} from "./pages/Home";
import {Movies} from "./pages/movies/Movies";
import {User} from "./pages/user/User";
import {Admin} from "./pages/Admin";
import {Search} from "./pages/Search";
import {Profile} from "./pages/Profile";

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
        name: "Search",
        path: "/search",
        component: Search
    },
    {
        name: "Profile",
        path: "/profile",
        component: Profile
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