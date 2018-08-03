import {
    GAME_ACTIVATE,
    PUSH_CHANNEL_UPDATE
} from "../actions";

const INITIAL_STATE = {
    gameList: {
        channels: [],
        rowCount: 0
    },
    currentChannel: null
};

export default function(state = INITIAL_STATE, action)
{
    switch (action.type)
    {
        case GAME_ACTIVATE:
            return {
                ... state,
                currentChannel: action.channel
            };

        case PUSH_CHANNEL_UPDATE:
        {
            const currentChannel = getCurrentChannel(state);

            const { channel: newChannel, hand: newHand } = action;

            if (currentChannel.id === newChannel.id)
            {
                const mergedChannel = {
                    ...newChannel,
                    current: {
                        ...newChannel.current,
                        hand: newHand
                    },
                    chatMessages: currentChannel.chatMessages.concat(newChannel.chatMessages)
                };

                //console.log("MERGED CHAT", currentChannel.chatMessages, newChannel.chatMessages)

                return {
                    ... state,
                    currentChannel: mergedChannel
                }

            }
            return state;
        }
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


