import {
    FOO_SET_LIST,
    FOO_UPDATE_DETAIL
} from "../actions";

const INITIAL_STATE = {
    // list of reduced foos (only a few properties populated)
    foos: [],

    // current foo detail object
    fooDetail: null,

    // Current nummber of available foos
    rowCount: 0
};

export default function(state = INITIAL_STATE, action)
{
    switch (action.type)
    {
        case FOO_SET_LIST:
        {
            const { foos, rowCount } = action;

            return {
                ... state,
                foos,
                rowCount
            }
        }
        case FOO_UPDATE_DETAIL:
        {
            const { foo } = action;

            return {
                ... state,
                fooDetail : foo
            }
        }
    }

    return state;
}



export function getFoos(state)
{
    return state.foos;
}



export function getFooDetail(state)
{
    return state.fooDetail;
}

export function getFooRowCount(state)
{
    return state.rowCount;
}

