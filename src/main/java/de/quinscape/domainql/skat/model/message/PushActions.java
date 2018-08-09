package de.quinscape.domainql.skat.model.message;

/**
 * Constants for redux actions we push from the server to the client.
 *
 * (see src/main/js/game/actions/index.js (PUSH_ACTIONS) )
 */
public final class PushActions
{
    private PushActions()
    {
        // no instances
    }

    public final static String PUSH_CHAT_MESSAGE = "PUSH_CHAT_MESSAGE";
    public final static String PUSH_CHANNEL_UPDATE = "PUSH_CHANNEL_UPDATE";
}
