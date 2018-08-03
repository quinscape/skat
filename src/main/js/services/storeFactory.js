import { connectRouter, routerMiddleware } from "connected-react-router";
import { applyMiddleware, createStore } from "redux";
import thunk from "redux-thunk";

function errorLogger(err)
{
    console.error("Thunk promise rejected", err);
}

function thunkErrorLogger(store)
{
    return next => action => {

        const result = next(action);
        if (typeof action === "function" && result instanceof Promise)
        {
            return result.catch(errorLogger);
        }
        return result;
    };
}


function logger(store)
{
    return next => action => {

        const result = next(action);
        console.log("ACTION " + action.type, {
            action,
            state: store.getState()
        });
        return result;
    };
}


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
            // Automatically log rejected promises returned from thunks
            thunkErrorLogger,
            // Support thunks for async actions
            thunk,
            // log all sync actions
            //logger
        )
    );
}
