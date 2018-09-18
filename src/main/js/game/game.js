import "whatwg-fetch"
import { Promise } from "es6-promise-polyfill"
import bootstrap from "jsview-bootstrap"
import React from "react"

import rootReducer, { getCurrentChannel } from "./reducers/index"
import { pushAction } from "./actions/index"
import FormConfigProvider from "domainql-form/lib/FormConfigProvider"

import storeFactory from "../services/storeFactory"
import createHistory from "history/createBrowserHistory";
import SkatCardsGame from "./SkatCardsGame";
import _config, { __initConfig } from "../services/config";

import { Provider } from "react-redux"
import loader from "../services/loader";
import Hub from "../services/hub";

const history = createHistory();

let _store;

bootstrap(
    initial => {

        console.info("INITIAL DATA", initial);

        __initConfig(initial, ["contextPath", "authentication", "csrfToken", "connectionId", "shufflingStrategies"]);

        // We need to tell webpack from where to load dynamically imported modules
        // noinspection JSUnresolvedVariable
        __webpack_public_path__ = _config().contextPath + "/js/";

        _store = storeFactory(
            rootReducer,
            // build initial redux state from static defaults and initial data (BUILD_INITIAL)
            {
                gameList: initial.gameList.currentGameList,
                calculator: null,
                userConfig: initial.userConfig.userConfig
            },
            history
        );

        Hub.register("PUSH_ACTION", ({payload, id}) => {

//            console.log("PUSH_ACTION", payload, "id = ", id);
            
            _store.dispatch(
                pushAction(payload, id)
            );
        });

        return Promise.all([
            Hub.init(initial.connectionId),
            loader([
                "/media/deck4.svg"
            ])
        ]).then( ([ connectionId, symbols]) => {

            console.log( "SYMBOLS", Object.keys(symbols).sort());

            return (
                <Provider store={_store}>
                    <FormConfigProvider
                        schema={initial.schema}
                    >
                        <SkatCardsGame
                            store={_store}
                            history={history}
                        />
                    </FormConfigProvider>
                </Provider>
            );
        });
    },
    () => console.info("ready!")
);

/**
 * Exported as "Skat.store()" into the browser env.
 */
export function store()
{
    return _store;
}

/**
 * Exported as "Skat.state()" into the browser env.
 */
export function state()
{
    return _store.getState();
}

/**
 * Export config service
 * 
 * @return {*}
 */
export function config()
{
    return _config();
}

export const PRELOADED_QUERIES = {
    // language=GraphQL
    gameList: `
        query currentGameList
        {
            currentGameList{
                channels{
                    id
                    gameInProgress                           
                    users{
                        name
                        type 
                    }
                }
                rowCount
            }
        }
    `,
    // language=GraphQL
    userConfig: `
        query userConfig
        {
            userConfig{
                id
                userId
                lockBidding
            }
        }
    `
};
