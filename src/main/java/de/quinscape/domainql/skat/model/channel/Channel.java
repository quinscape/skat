package de.quinscape.domainql.skat.model.channel;

import de.quinscape.domainql.skat.model.core.GamePhase;
import de.quinscape.domainql.skat.model.core.GameRound;
import de.quinscape.domainql.skat.model.core.SkatHand;
import de.quinscape.domainql.skat.model.message.ChannelUpdateAction;
import de.quinscape.domainql.skat.model.message.OutgoingMessage;
import de.quinscape.domainql.skat.model.message.OutgoingMessageType;
import de.quinscape.domainql.skat.model.user.GameUser;
import de.quinscape.domainql.skat.runtime.game.HandFetcher;
import de.quinscape.domainql.skat.runtime.message.PreparedMessages;
import de.quinscape.spring.jsview.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.svenson.JSONProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Channel
{
    private final static Logger log = LoggerFactory.getLogger(Channel.class);

    private final String id;

    private List<GameUser> users = new ArrayList<>();

    private List<String> owners = new ArrayList<>();

    private List<LogEntry> logEntries = new ArrayList<>();

    /**
     *  old CARD entries to be removed via update
     */
    private List<Integer> removedLogEntries;

    private GameRound current;

    private boolean publicChannel;


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


    public List<LogEntry> getLogEntries()
    {
        return logEntries;
    }


    public void setLogEntries(List<LogEntry> logEntries)
    {
        this.logEntries = logEntries;
    }


    public GameRound getCurrent()
    {
        return current;
    }


    public void setCurrent(GameRound current)
    {
        this.current = current;
    }


    public void setPublic(boolean publicChannel)
    {
        this.publicChannel = publicChannel;
    }


    public boolean isPublic()
    {
        return publicChannel;
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


    public synchronized PreparedMessages prepareMessage(GameUser exclude, OutgoingMessage outgoingMessage)
    {
        log.debug("Preparing message: {} (exclude = {})", outgoingMessage, exclude);

        final PreparedMessages preparedMessages;
        preparedMessages = new PreparedMessages();
        for (GameUser user : getUsers())
        {
            if (user.isActive() && !user.equals(exclude))
            {
                preparedMessages.add(
                    user.getConnectionId(),
                    outgoingMessage
                );
            }
        }
        return preparedMessages;
    }

    public synchronized PreparedMessages prepareUpdate(GameUser exclude, LogEntry... description)
    {
        log.debug("Preparing update: {} (exclude = {})", description, exclude);

        final boolean haveMessages = description != null && description.length > 0;
        List<LogEntry> messages = null;
        if (haveMessages)
        {
            messages = Arrays.asList(description);
            this.getLogEntries().addAll(messages);
        }
        final Channel minimized = this.getMinimizedCopy();
        if (haveMessages)
        {
            minimized.getLogEntries().addAll(messages);
        }

        return minimized.prepareUpdate(usersExcluding(exclude));
    }

    public List<GameUser> usersExcluding(GameUser exclude)
    {
        final List<GameUser> users = new ArrayList<>(getUsers());
        users.remove(exclude);
        return users;
    }

    public synchronized PreparedMessages prepareUpdate(List<GameUser> users)
    {
        final PreparedMessages preparedMessages;
        preparedMessages = new PreparedMessages();
        for (GameUser user : users)
        {
            if (user.isActive())
            {
                final SkatHand hand = HandFetcher.getHand(
                    getCurrent(),
                    user.getConnectionId()
                );

                log.debug("Notifying {}, minimized ={}, hand = {}", user, this, hand);

                final OutgoingMessage outgoingMessage = new OutgoingMessage(
                    OutgoingMessageType.PUSH_ACTION,
                    new ChannelUpdateAction(
                        this,
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

    @JSONProperty(ignore = true)
    public Channel getMinimizedCopy()
    {
        final Channel channel = new Channel(id);
        channel.setUsers(getUsers());
        channel.setCurrent(getCurrent());
        channel.setOwners(getOwners());
        channel.setPublic(isPublic());
//        channel.setHistory(getHistory());
//        channel.setLogEntries(getLogEntries());
        return channel;
    }


    /**
     * Makes sure that there are at most 3 log entries with card property in the log entries. The skat rules state that
     * players may look up the last trick, but not more, so we make sure that only the last trick is visible after we
     * finished a trick.
     *
     * @return list with ids of removed entries.
     */
    public List<Integer> flushGameLogEntries()
    {
        if (log.isDebugEnabled())
        {
            log.debug("Before flush: {}", JSONUtil.DEFAULT_GENERATOR.forValue(logEntries));
        }

        final int last = logEntries.size() - 1;
        int cardEntryCount = 0;
        int winEntryCount = 0;

        final List<LogEntry> newEntries = new ArrayList<>(logEntries.size());
        final List<Integer> removed = new ArrayList<>();
        for (int i = last ; i >= 0; i--)
        {
            final LogEntry logEntry = logEntries.get(i);
            if (logEntry.getType() == EntryType.CARD)
            {
                cardEntryCount++;
                if (cardEntryCount > 3)
                {
                    removed.add(logEntry.getId());
                    // skip more than 3 cards
                    continue;
                }
            }
            else if (logEntry.getType() == EntryType.WIN)
            {
                winEntryCount++;
                if (winEntryCount > 1)
                {
                    removed.add(logEntry.getId());
                    // skip more than 1 win entries
                    continue;
                }
            }

            newEntries.add(
                0,
                logEntry
            );
        }
        logEntries = newEntries;

        if (log.isDebugEnabled())
        {
            log.debug("After flush: {}", JSONUtil.DEFAULT_GENERATOR.forValue(logEntries));
        }

        return removed;
    }


    public void setRemovedLogEntries(List<Integer> removedLogEntries)
    {
        this.removedLogEntries = removedLogEntries;
    }

    public List<Integer> getRemovedLogEntries()
    {
        if (removedLogEntries == null)
        {
            return Collections.emptyList();
        }
        return removedLogEntries;
    }
}
