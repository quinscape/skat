import { connectRouter, routerMiddleware } from "connected-react-router";
import { applyMiddleware, createStore } from "redux";
import thunk from "redux-thunk";

/**
 * Factory to create the actual redux store. Can be modified to apply local middle ware
 *
 * @param {function} rootReducer    root reducer function
 * @param initialState              initial redux state
 * @param history                   history object
 * 
 * @return {Store<any> & {dispatch: any}}
 */
export default function (rootReducer, initialState, history)
{

    return createStore(
        connectRouter(history)(rootReducer),
        initialState,
        applyMiddleware(
            // connect react-router and redux
            routerMiddleware(history),
            // Support thunks for async actions
            thunk
        )
    );
}
