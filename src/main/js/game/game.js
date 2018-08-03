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
import config, { __initConfig } from "../services/config";

import { Provider } from "react-redux"
import loader from "../services/loader";
import Hub from "../services/hub";


const history = createHistory();

bootstrap(
    initial => {

        console.info("INITIAL DATA", initial);

        __initConfig(initial, ["contextPath", "authentication", "csrfToken", "connectionId"]);

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

        Hub.register("PUSH_ACTION", ({payload, id}) => {

            //console.log("PUSH_ACTION", payload, "id = ", id);
            
            store.dispatch(
                pushAction(payload, id)
            );
        });

        return Promise.all([
            Hub.init(initial.connectionId),
            loader([
                "/media/deck-simpler.svg"
            ])
        ]).then( ([ connectionId, symbols]) => {


            //console.log("SYMBOLS", symbols);

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
                    users{
                        name
                        type 
                    }
                }
                rowCount
            }
        }
    `
};
 
