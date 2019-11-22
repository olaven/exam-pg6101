import {Home} from "../components/pages/home/Home";
import {User} from "../components/pages/user/User";
import {Search} from "../components/pages/search/Search";
import {Profile} from "../components/pages/profile/Profile";

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
        path: "/profile/:email",
        component: Profile
    },
    {
        name: "Login/Signup",
        path: "/login",
        component: User
    }
];