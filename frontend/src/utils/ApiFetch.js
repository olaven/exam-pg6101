/**
 * This file defines a wrapper for the fetch method.
 * It's main purpose is to hide the switching between
 * prod. and dev. environments from other code.
 * */


export const ApiFetch = (path, config) => {

    let devPort = null;

    if (path.includes("authentication")) {
        devPort = 8081;
    } else if (path.includes("graphql")) {
        devPort = 8083;
    } else {
        devPort = 8080;
    }

    const base = process.env.NODE_ENV === "production" ?
        "http://localhost:80/api" : //gateway
        "http://localhost:" + devPort;


    if (!path.includes("graphql")) {
        /*
        * GraqhQL does not support credentials.
        * Only include credentials if _not_ going to /graphql
        * */
        config = { ...config, credentials: "include"}
    }

    return fetch(base + path, config);
};