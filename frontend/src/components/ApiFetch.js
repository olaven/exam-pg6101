/**
 * This file defines a wrapper for the fetch method.
 * It's main purpose is to hide the switching between
 * prod. and dev. environments from other code.
 * */


export const ApiFetch = (path, config) => {

    const base = process.env.NODE_ENV === "production" ?
        "http://localhost:80/api/" : //gateway
        "http://localhost:8080/";

    return fetch(base + path, {
        ...config,
        credentials: "include" //needed to send cookies
    });
};