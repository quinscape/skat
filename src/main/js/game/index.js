import bootstrap from "jsview-bootstrap"
import React from "react"

import rootReducer from "./reducers/index"
import FormConfigProvider from "domainql-form/lib/FormConfigProvider"

import storeFactory from "../services/store-factory"
import createHistory from "history/createBrowserHistory";
import SkatCardsGame from "./SkatCardsGame";
import config, { __initConfig } from "../services/config";

import { Provider } from "react-redux"


const history = createHistory();

bootstrap(
    initial => {

        console.info("INITIAL DATA", initial);

        __initConfig(initial, ["contextPath", "authentication", "csrfToken"]);

        const store = storeFactory(
            rootReducer,
            initial,
            history
        );

        // We need to tell webpack from where to load dynamically imported modules
        // noinspection JSUnresolvedVariable
        __webpack_public_path__ = config().contextPath + "/js/";

        return (
            <Provider store={ store }>
                <FormConfigProvider
                    schema={ initial.schema }
                >
                    <SkatCardsGame
                        store={store}
                        history={history}
                    />
                </FormConfigProvider>
            </Provider>
        );
    },
    () => console.info("ready!")
);
