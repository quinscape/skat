import "whatwg-fetch"
import { Promise } from "es6-promise-polyfill"
import bootstrap from "jsview-bootstrap"
import React from "react"

import rootReducer from "./reducers/index"
import FormConfigProvider from "domainql-form/lib/FormConfigProvider"

import storeFactory from "../services/store-factory"
import createHistory from "history/createBrowserHistory";
import SkatCardsGame from "./SkatCardsGame";
import config, { __initConfig } from "../services/config";

import { Provider } from "react-redux"
import loader from "../services/loader";


const history = createHistory();

bootstrap(
    initial => {

        console.info("INITIAL DATA", initial);

        __initConfig(initial, ["contextPath", "authentication", "csrfToken", "windowId"]);

        // We need to tell webpack from where to load dynamically imported modules
        // noinspection JSUnresolvedVariable
        __webpack_public_path__ = config().contextPath + "/js/";

        const store = storeFactory(
            rootReducer,
            {
                gameList: initial.gameList.currentGameList
            },
            history
        );

        return loader([
            "/media/deck.svg"
        ]).then(symbols => {

            console.log(symbols);

            return (
                <Provider store={store}>
                    <FormConfigProvider
                        schema={initial.schema}
                    >
                        <SkatCardsGame
                            store={store}
                            history={history}
                        />
                    </FormConfigProvider>
                </Provider>
            );
        });
    },
    () => console.info("ready!")
);

export const PRELOADED_QUERIES = {
    // language=GraphQL
    gameList: `
        {
            currentGameList{
                channels{
                    id
                    gameInProgress                           
                    users
                }
                rowCount
            }
        }
    `
};
