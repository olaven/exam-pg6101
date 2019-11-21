import * as React from "react";
import {PaginationFetcher} from "../../utils/PaginationFetcher";



export const TimelineContext = React.createContext({});

export const TimelineContextProvider = props => {

    const [location, setLocation] = React.useState(null);
    const [basePath, setBasePath] = React.useState(null);
    const [refreshTrigger, setRefreshTrigger] = React.useState(0);
    
    const messagePage = PaginationFetcher(location, basePath, refreshTrigger);

    const triggerRefresh = () => {

        const updated = refreshTrigger + 1;
        setRefreshTrigger(updated);
    };


    return <TimelineContext.Provider value={{setLocation, setBasePath, messagePage, triggerRefresh}}>
        {props.children}
    </TimelineContext.Provider>
};