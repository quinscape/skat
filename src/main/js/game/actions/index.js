import { push } from "connected-react-router"
import graphql, { defaultErrorHandler } from "../../services/graphql";

export const SKAT_USER_NAME = "SKAT_USER_NAME";

export const GAME_ACTIVATE = "GAME_UPDATE_ACTIVATE";
export const CALCULATOR_SETTINGS_STORE = "CALCULATOR_SETTINGS_STORE";

// PUSH_ actions are pushed from the server via websocket (PUSH_ACTIONS)
export const PUSH_CHANNEL_UPDATE = "PUSH_CHANNEL_UPDATE";
export const USER_CONFIG_UPDATE = "USER_CONFIG_UPDATE";
export const HAND_REPLACE = "HAND_REPLACE";


function updateErrorHandler(err)
{
    console.error("Error as result of action producer", err)
}

export function activateGame(channel)
{
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
//            console.log("Ignored " + ignore + " repeats");
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

//                console.log("CREATE_GAME_MUTATION", createGame);

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
    mutation joinGame($channelId : String, $userName: String)
    {
        joinGame(secret: $channelId, userName: $userName)
    }
`;

export function joinGame(id)
{
    return (dispatch, getState) => {

        const userName = sessionStorage.getItem(SKAT_USER_NAME);

        return graphql({
            query: JOIN_GAME_MUTATION,
            variables: {
                channelId: id,
                userName
            }
        }).then(
            ({ joinGame }) => {

               if (joinGame)
               {
                   console.log("Logged in as " + joinGame);

                   sessionStorage.setItem(SKAT_USER_NAME, joinGame);

                   dispatch(
                       push("/game/" + id)
                   );
               }
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
        })
        .catch(defaultErrorHandler);

    };
}

export function reshuffle(channelId)
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
                channelId
            }
        })
        .catch(defaultErrorHandler);


    };
}

export function deal(channelId)
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
                channelId
            }
        })
        .catch(defaultErrorHandler);

    };
}

export function accept(channelId, value)
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
                channelId,
                value
            }
        })
        .catch(defaultErrorHandler);

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
                channelId
            }
        })
        .catch(defaultErrorHandler);
    };
}



export function storeCalculatorSettings(calculator)
{
    return {
        type: CALCULATOR_SETTINGS_STORE,
        calculator
    };
}

export function sendChatMessage(channelId, message)
{
    return () => {

        // new log entry is pushed back from server as PUSH_CHANNEL_UPDATE
        return graphql({
            // language=GraphQL
            query: `
                mutation sendChatMessage($channelId: String, $message: String)
                {
                    sendChatMessage(secret: $channelId, message: $message)
                }
            `,
            variables: {
                channelId,
                message
            }
        })
        .catch(defaultErrorHandler);
    }
}

export function storeUserConfig(userConfig)
{
//    console.log("storeUserConfig", userConfig);

    return (dispatch, getState) => {

        return graphql({
            // language=GraphQL
            query: `
                mutation storeUserConfig($userConfig: UserConfigInput)
                {
                    storeUserConfig(userConfig: $userConfig)
                    {
                        id
                        userId
                        lockBidding
                    }
                }
            `,
            variables: {
                userConfig
            }
        })
            .then(
                ({ storeUserConfig }) => {

                    dispatch({
                        type: USER_CONFIG_UPDATE,
                        userConfig: storeUserConfig
                    })
                },
                defaultErrorHandler
            );
    }
}

export function pickUpSkat(channelId)
{
    return (dispatch, getState) => {

        return graphql({
            // language=GraphQL
            query: `
                mutation pickUpSkat($channelId: String)
                {
                    pickUpSkat(secret: $channelId)
                }
            `,
            variables: {
                channelId
            }
        })
        .catch(defaultErrorHandler);
    }
}

const GameType = [
    "SUIT_CLUBS",
    "SUIT_SPADES",
    "SUIT_HEARTS",
    "SUIT_DIAMONDS",
    "NULL",
    "GRAND",
    "RAMSCH"
];

const AnnouncementName = [
    "HAND",
    "SCHNEIDER_ANNOUNCED",
    "NO_TRICKS_ANNOUNCED",
    "OUVERT"
];

export function declareGame(channelId, gameValue, skatA, skatB)
{

    return (dispatch, getState) => {

        const gameDeclaration = {
            gameType: gameValue.gameType,
            hand: gameValue.hand,
            ouvert: gameValue.ouvert,
            announcement: gameValue.announcement
        };

        //console.log("GAME DECLARATION", gameDeclaration);

        return graphql({
            // language=GraphQL
            query: `
                mutation declareGame($channelId: String, $gameDeclaration: GameDeclarationInput, $skatA: Int, $skatB: Int)
                {
                    declareGame(
                        secret: $channelId,
                        gameDeclaration: $gameDeclaration
                        skatA: $skatA,
                        skatB: $skatB
                    )
                }
            `,
            variables: {
                channelId,
                gameDeclaration,
                skatA,
                skatB
            }
        })
        .catch(defaultErrorHandler);
    }

}

export function playCard(channelId, card)
{
    return (dispatch, getState) => {

        return graphql({
            // language=GraphQL
            query: `
                mutation playCard($channelId: String, $card: Int)
                {
                    playCard(
                        secret: $channelId,
                        card: $card
                    )
                }
            `,
            variables: {
                channelId,
                card
            }
        })
        .catch(defaultErrorHandler);
    }
}

export function startNewRound(channelId)
{
    return (dispatch, getState) => {

        return graphql({
            // language=GraphQL
            query: `
                mutation startNewRound($channelId: String)
                {
                    startNewRound(
                        secret: $channelId
                    )
                }
            `,
            variables: {
                channelId
            }
        })
        .catch(defaultErrorHandler);
    }
}
