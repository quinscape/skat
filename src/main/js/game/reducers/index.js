import {
    GAME_ACTIVATE
} from "../actions";

const INITIAL_STATE = {
    gameList: {
        channels: [],
        rowCount: 0
    }
};

export default function(state = INITIAL_STATE, action)
{
    console.log(action);

    switch (action.type)
    {
        case GAME_ACTIVATE:
            return {
                ... state,
                current: action.skatGame
            }
    }

    return state;
}


export function getGameList(state)
{
    return state.gameList;
}

export function getCurrentGame(state)
{
    return state.current;
}


