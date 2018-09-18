import {
    CALCULATOR_SETTINGS_STORE,
    GAME_ACTIVATE,
    PUSH_CHANNEL_UPDATE,
    HAND_REPLACE,
    USER_CONFIG_UPDATE,
    OPTIONS_STORE
} from "../actions";
import { getCalculatorDefaults } from "../../components/calculator/Calculator";

import update from "immutability-helper"


function removeEntries(logEntries, removedLogEntries)
{
    if (!removedLogEntries || removedLogEntries.length === 0)
    {
        return logEntries;
    }

    const out = [];
    for (let i = 0; i < logEntries.length; i++)
    {
        const e = logEntries[i];
        if (removedLogEntries.indexOf(e.id) < 0)
        {
            out.push(e);
        }
    }

    return out;
}

function skatPickedUpIn(currentChannel)
{
    return currentChannel && currentChannel.current.bidding ?
        currentChannel.current.bidding.skatPickedUp :
        null;
}

// default state built in game.js (BUILD_INITIAL)
export default function(state = null, action)
{
    switch (action.type)
    {


        case GAME_ACTIVATE:
        {
            const { channel } = action;
            const { current: { hand } } = channel;

            return {
                ... state,
                currentChannel: action.channel,
                calculator: getCalculatorDefaults(hand)
            };
        }
        case CALCULATOR_SETTINGS_STORE:

            return {
                ... state,
                calculator: action.calculator
            };

        case PUSH_CHANNEL_UPDATE:
        {
            const currentChannel = getCurrentChannel(state);

            const { channel: newChannel, hand: newHand } = action;

            if (!currentChannel || currentChannel.id === newChannel.id)
            {
                const logEntries = currentChannel ? currentChannel.logEntries.concat(newChannel.logEntries) : newChannel.logEntries;
                const mergedChannel = {
                    ...newChannel,
                    current: {
                        ...newChannel.current,
                        hand: newHand
                    },
                    logEntries: removeEntries(logEntries, newChannel.removedLogEntries)
                };

                //console.log("MERGED CHAT", currentChannel.logEntries, newChannel.logEntries)

                const skatPickedUpChange = skatPickedUpIn(currentChannel) !== skatPickedUpIn(newChannel);

                return {
                    ... state,
                    currentChannel: mergedChannel,
                    calculator: !state.calculator || skatPickedUpChange ? getCalculatorDefaults(newHand) : state.calculator
                }
            }
            return state;
        }
        case USER_CONFIG_UPDATE:
        {
            return {
                ... state,
                userConfig: action.userConfig
            };
        }

        case HAND_REPLACE:

            return update(state, {
                currentChannel: {
                    current: {
                        hand: { $set: action.hand }
                    }
                }
            });

        case OPTIONS_STORE:

            return update(state, {
                currentChannel: {
                    current: {
                        options: { $set: action.options }
                    }
                }
            });
    }

    return state;
}


export function getGameList(state)
{
    return state.gameList;
}

export function getCurrentChannel(state)
{
    return state.currentChannel;
}


export function getCalculatorData(state)
{
    return state.calculator;
}


export function getCalculatorResult(state)
{
    const calculator = getCalculatorData(state);
    return calculator ? calculator.result : null;
}

export function getUserConfig(state)
{
    return state.userConfig;
}
