import { push } from "connected-react-router"
import graphql, { defaultErrorHandler } from "../../services/graphql";


export const GAME_ACTIVATE = "GAME_UPDATE_ACTIVATE";

// PUSH_ actions are pushed from the server via websocket
export const PUSH_CHANNEL_UPDATE = "PUSH_CHANNEL_UPDATE";


function updateErrorHandler(err)
{
    console.error("Error as result of action producer", err)
}

export function activateGame(channel)
{
    console.log("activateGame", channel);

    return {
        type: GAME_ACTIVATE,
        channel: channel
    };
}

let limit = -Infinity;
let ignore = 0;

export function pushAction(action, id)
{
    return (dispatch, getState ) => {

        if (id <= limit)
        {
            //console.log("Ignore repeat", action, id)
            ignore++;
            return ;
        }

        limit = id;
        if (ignore > 0)
        {
            console.log("Ignored " + ignore + " repeats");
            ignore = 0;
        }

        const { type } = action;

        if ( String(type).indexOf("PUSH_") !== 0)
        {
            throw new Error("Invalid pushed action type: " + type);
        }

        dispatch(action);
    }
}

export function createGame(channelId, isPublic = true)
{
    return (dispatch, getState) => {

        return graphql({
            // language=GraphQL
            query: `
                mutation createGame($channelId : String, $isPublic: Boolean)
                {
                    createGame( isPublic: $isPublic, secret: $channelId )
                    {
                        id
                    }
                }
            `,
            variables: {
                channelId,
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
    mutation joinGame($channelId : String)
    {
        joinGame(secret: $channelId)
        {
            id
            public
            users{
                name
                type 
                connectionId
                active
            }
            owners
            chatMessages{
                message
                timestamp
                user
            }
            current {
                bidding{
                    bids{
                        position
                        value
                    }
                    bidder
                    responder
                    nextValue
                    declarer
                }
                multipliers
                seating{
                    name
                    type 
                    connectionId
                    active
                }
                numberOfSeats
                hand{
                    cards
                    gameUser{
                        name
                    }
                    currentPosition
                }
                phase
                currentDealer
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
                channelId: id
            }
        }).then(
            ({joinGame}) => {

                dispatch(
                    activateGame(joinGame)
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

export function reshuffle(channedId)
{
    return (dispatch, getState) => {

        return graphql({
            // language=GraphQL
            query: `
                mutation reshuffle($channelId: String)
                {
                    reshuffle(secret: $channelId)
                }
            `,
            variables: {
                channelId: channedId
            }
        });

    };
}

export function deal(channedId)
{
    return (dispatch, getState) => {

        return graphql({
            // language=GraphQL
            query: `
                mutation deal($channelId: String)
                {
                    deal(secret: $channelId)
                }
            `,
            variables: {
                channelId: channedId
            }
        });

    };
}

export function accept(channedId, value)
{
    return (dispatch, getState) => {

        return graphql({
            // language=GraphQL
            query: `
                mutation accept($channelId: String, $value: Int)
                {
                    accept(secret: $channelId, value: $value)
                }
            `,
            variables: {
                channelId: channedId,
                value
            }
        });
    };
}

export function pass(channelId)
{
    return (dispatch, getState) => {

        return graphql({
            // language=GraphQL
            query: `
                mutation pass($channelId: String)
                {
                    pass(secret: $channelId)
                }
            `,
            variables: {
                channelId: channelId
            }
        });
    };
}
