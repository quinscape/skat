package de.fforw.skat.model.channel;

import de.fforw.skat.model.GamePhase;
import de.fforw.skat.model.GameRound;
import de.fforw.skat.model.GameUser;
import de.fforw.skat.model.SkatHand;
import de.fforw.skat.runtime.HandFetcher;
import de.fforw.skat.runtime.message.OutgoingMessage;
import de.fforw.skat.runtime.message.OutgoingMessageType;
import de.fforw.skat.runtime.message.PreparedMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.svenson.JSONTypeHint;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Channel
{
    private final static Logger log = LoggerFactory.getLogger(Channel.class);


    private final String id;

    private List<GameUser> users = new ArrayList<>();

    private List<String> owners = new ArrayList<>();

    private List<ChatMessage> chatMessages = new ArrayList<>();

    private List<GameRound> history = new ArrayList<>();

    private GameRound current;

    private boolean publicGame;


    public Channel()
    {
        this(null);
    }


    public Channel(String id)
    {
        this.id = id;
    }


    public String getId()
    {
        return id;
    }


    public List<GameUser> getUsers()
    {
        return users;
    }


    public void setUsers(List<GameUser> users)
    {
        this.users = users;
    }


    public List<ChatMessage> getChatMessages()
    {
        return chatMessages;
    }


    public void setChatMessages(List<ChatMessage> chatMessages)
    {
        this.chatMessages = chatMessages;
    }


    @JSONTypeHint(GameRound.class)
    public List<GameRound> getHistory()
    {
        return history;
    }


    public void setHistory(List<GameRound> history)
    {
        this.history = history;
    }


    public GameRound getCurrent()
    {
        return current;
    }


    public void setCurrent(GameRound current)
    {
        this.current = current;
    }


    public void setPublic(boolean publicGame)
    {
        this.publicGame = publicGame;
    }


    public boolean isPublic()
    {
        return publicGame;
    }


    public List<String> getOwners()
    {
        return owners;
    }


    public void setOwners(List<String> owners)
    {
        this.owners = owners;
    }


    public ChannelListing getListing()
    {
        return new ChannelListing(id, users, current != null && current.getPhase() != GamePhase.OPEN);
    }


    public synchronized PreparedMessages prepareUpdate(GameUser exclude, String description)
    {
        log.debug("Preparing update: {} (exclude = {})", description, exclude);

        final Channel minimized = this.minimize();

        final ChatMessage message = new ChatMessage();
        message.setMessage(description);
        message.setTimestamp(Instant.now().toString());
        message.setUser("SYSTEM");

        minimized.getChatMessages().add(message);
        this.getChatMessages().add(message);

        final PreparedMessages preparedMessages;
        preparedMessages = new PreparedMessages();
        for (GameUser user : getUsers())
        {
            if (user.isActive() && !user.equals(exclude))
            {
                final SkatHand hand = HandFetcher.getHand(
                    current,
                    user.getConnectionId()
                );

                log.debug("Notifying {}, minimized ={}, hand = {}", user, minimized, hand);

                final OutgoingMessage outgoingMessage = new OutgoingMessage(
                    OutgoingMessageType.PUSH_ACTION,
                    new ChannelUpdateAction(
                        minimized,
                        hand
                    )
                    , null
                );
                preparedMessages.add(
                    user.getConnectionId(),
                    outgoingMessage
                );
            }
        }

        return preparedMessages;
    }


    private Channel minimize()
    {
        final Channel channel = new Channel(id);
        channel.setUsers(getUsers());
        channel.setCurrent(getCurrent());
        channel.setOwners(getOwners());
        channel.setPublic(isPublic());
//        channel.setHistory(getHistory());
//        channel.setChatMessages(getChatMessages());
        return channel;
    }

}
