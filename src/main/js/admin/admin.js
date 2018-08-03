import React from "react"

import rootReducer from "./reducers/index"

import storeFactory from "../services/storeFactory"
import bootstrap from "jsview-bootstrap"
import createHistory from "history/createBrowserHistory";
import AdminApp from "./AdminApp";
import { __initConfig } from "../services/config";


const history = createHistory();

bootstrap(
    initial=> {

        __initConfig(initial);

        const store = storeFactory(
            rootReducer,
            initial,
            history
        );

        // We need to tell webpack from where to load dynamically imported modules
        // noinspection JSUnresolvedVariable
        __webpack_public_path__ = initial.contextPath + "/js/";

        return (
            <AdminApp
                store={store}
                history={history}
            />
        );
    },
    () => console.info("ready!")
);

export const PRELOADED_QUERIES = {
    // language=GraphQL
    preloadedUsers: `{
        listUsers {
            id
            login
            disabled
            created
            lastLogin
            roles
            foos{
                id
                name
            }
        }
    }
    `
};
