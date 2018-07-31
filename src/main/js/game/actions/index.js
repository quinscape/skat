import { push } from "connected-react-router"
import graphql, { defaultErrorHandler } from "../../services/graphql";


export const GAME_CREATE = "GAME_CREATE";
export const GAME_JOIN = "GAME_JOIN";
export const GAME_ACTIVATE = "GAME_UPDATE_ACTIVATE";

function updateErrorHandler(err)
{
    console.error("Error as result of action producer", err)
}

// language=GraphQL
const CREATE_GAME_MUTATION = `
    mutation createGame($secret : String, $isPublic: Boolean, $windowId: String)
    {
        createGame( windowId: $windowId, isPublic: $isPublic, secret: $secret )
        {
            id
        }
    }
`;

export function activeGame(createGame)
{
    return {
        type: GAME_ACTIVATE,
        skatGame: createGame
    };
}

export function createGame(secret, isPublic = true)
{
    return (dispatch, getState) => {

        return graphql({
            query: CREATE_GAME_MUTATION,
            variables: {
                secret,
                isPublic
            }
        }).then(
            ({createGame}) => {

                console.log("CREATE_GAME_MUTATION", createGame);

                dispatch(
                    joinGame(createGame.id)
                );

            },
            defaultErrorHandler
        );
    };
}

// language=GraphQL
const JOIN_GAME_MUTATION = `
    mutation joinGame($secret : String, $windowId: String)
    {
        joinGame(secret: $secret, windowId: $windowId)
        {
            id
            public
            chatMessages{
                message
                timestamp
                user
            }
            current {
                biddingResult
                multipliers
                seating
                hand
                phase
                currentDealer
                currentPosition
            }
            history{
                _sig
                biddingResult
                initialStack
                multipliers
                seating
            }
        }
    }
`;

export function joinGame(id)
{
    return (dispatch, getState) => {

        return graphql({
            query: JOIN_GAME_MUTATION,
            variables: {
                secret: id
            }
        }).then(
            ({joinGame}) => {

                dispatch(
                    activeGame(joinGame)
                );

                dispatch(
                    push("/game/" + joinGame.id)
                );
            },
            defaultErrorHandler
        );
    }
}

export function flushGames()
{
    return (dispatch, getState) => {

        return graphql({
            // language=GraphQL
            query: `
                mutation flushGames
                {
                    flushGames
                }
            `
        });

    };
}
