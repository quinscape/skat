package de.quinscape.domainql.skat.runtime.service;

import de.quinscape.domainql.skat.model.channel.Channel;
import de.quinscape.domainql.skat.model.channel.ChannelListing;
import de.quinscape.domainql.skat.model.channel.ChannelListings;
import de.quinscape.domainql.skat.model.channel.LogEntry;
import de.quinscape.domainql.skat.model.core.GamePhase;
import de.quinscape.domainql.skat.model.core.GameRound;
import de.quinscape.domainql.skat.model.user.GameUser;
import de.quinscape.domainql.skat.model.user.GameUserType;
import de.quinscape.domainql.skat.runtime.game.ChannelComparator;
import de.quinscape.domainql.skat.runtime.message.PreparedMessages;
import de.quinscape.domainql.skat.ws.SkatClientConnection;
import de.quinscape.domainql.skat.ws.SkatWebSocketHandler;
import de.quinscape.domainql.annotation.GraphQLField;
import de.quinscape.domainql.annotation.GraphQLLogic;
import de.quinscape.domainql.annotation.GraphQLMutation;
import de.quinscape.domainql.annotation.GraphQLQuery;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@GraphQLLogic
public class ChannelLogic
{
    private final static Logger log = LoggerFactory.getLogger(ChannelLogic.class);

    private final DSLContext dslContext;

    private final ChannelRepository channelRepository;

    private final Random random;

    private final SkatWebSocketHandler skatWebSocketHandler;

    private final ShufflingService shufflingService;


    @Autowired
    public ChannelLogic(
        DSLContext dslContext,
        ChannelRepository channelRepository,
        Random random,
        SkatWebSocketHandler skatWebSocketHandler,
        ShufflingService shufflingService
    )
    {
        this.dslContext = dslContext;
        this.channelRepository = channelRepository;
        this.random = random;
        this.skatWebSocketHandler = skatWebSocketHandler;
        this.shufflingService = shufflingService;
    }


    /**
     * the websocket channel updates are routed through this to provide the same authentication related view on
     * the current channel.
     *
     * @param channel channel input object
     * @return
     */
    @GraphQLQuery
    public Channel filterChannel(Channel channel)
    {
        /**
         *  just output, the auth logic is in the specialized fetchers
         *
         * @see GameRound#getInitialStack()
         * @see GameRound#getHand()
         */
        return channel;
    }


    @GraphQLQuery
    public ChannelListings currentGameList(
        @GraphQLField(defaultValue = "0")
            int offset,
        @GraphQLField(defaultValue = "20")
            int limit
    )
    {
        log.debug("currentGameList({}, {})", offset, limit);

        final List<ChannelListing> channels = channelRepository.listPublic();
        final ChannelListings channelListings = new ChannelListings();

        final int rowCount = channels.size();

        channelListings.setRowCount(rowCount);

        channels.sort(new ChannelComparator());


        if (rowCount > limit)
        {
            channelListings.setChannels(channels.subList(offset, offset + limit));
        }
        else
        {
            channelListings.setChannels(channels);
        }

        return channelListings;
    }


    @GraphQLMutation
    public boolean joinGame(SkatClientConnection conn, String secret)
    {

        final String connectionId = conn.getConnectionId();

        log.debug("joinGame(secret = {}, connectionId = {})", secret, connectionId);

        final AppAuthentication auth = AppAuthentication.current();
        Channel channel = getChannel(secret);

        PreparedMessages preparedMessages = null;
        synchronized (channel)
        {

            final List<GameUser> users = channel.getUsers();
            final List<GameUser> seating = channel.getCurrent().getSeating();
            if (
                users.stream().noneMatch(
                    u -> u.getConnectionId().equals(connectionId)
                )
            )
            {
                final GameUser newUser = GameUser.fromAuth(auth, connectionId);

                final GameRound current = channel.getCurrent();
                if (current.getPhase() == GamePhase.OPEN || newUser.getType() == GameUserType.TEST_USER)
                {
                    boolean replaced = false;
                    for (int i = 0; i < users.size(); i++)
                    {
                        GameUser currentUser = users.get(i);

                        // if the current user is inactive and equal to us
                        final boolean currentIsTest = currentUser.getType() == GameUserType.TEST_USER;
                        final boolean newIsTest = newUser.getType() == GameUserType.TEST_USER;
                        if (
                            !currentUser.isActive() && (
                                currentUser.equals(newUser) || (currentIsTest && newIsTest)
                            )
                        )
                        {
                            // we replace them in "users"..
                            users.set(i, newUser);

                            // .. and replace them by name in seating, too.
                            for (int j = 0; j < seating.size(); j++)
                            {
                                GameUser currentSeat = seating.get(j);
                                if (currentSeat.getName().equals(currentUser.getName()))
                                {
                                    seating.set(j, newUser);
                                    break;
                                }
                            }
                            replaced = true;
                            break;
                        }
                    }

                    if (!replaced)
                    {
                        users.add(
                            newUser
                        );

                        final int nullPos = seating.indexOf(null);
                        if (nullPos >= 0)
                        {
                            log.debug("Replace null user at #", nullPos);
                            seating.set(nullPos, newUser);
                        }
                        else if (seating.size() < current.getNumberOfSeats())
                        {
                            log.debug("Add user");
                            seating.add(newUser);
                        }
                    }

                    current.setLastUpdated(Instant.now().toString());

                    preparedMessages = channel.prepareUpdate(
                        newUser,
                        LogEntry.action(newUser.getName(),  "joined the channel")
                    );

                    final Channel minimized = channel.getMinimizedCopy();
                    minimized.getLogEntries().addAll(channel.getLogEntries());

                    preparedMessages.addAll(
                        minimized.prepareUpdate(
                        Collections.singletonList(newUser)
                        ).getMessages()
                    );
                }
            }
            channelRepository.updateChannel(channel);
        }

        if (preparedMessages != null)
        {
            preparedMessages.sendAll(skatWebSocketHandler);
        }

        return true;
    }

    private Channel getChannel(String secret)
    {
        Channel channel = channelRepository.getChannelById(secret);
        if (channel == null)
        {
            throw new IllegalStateException("Channel '" + secret + "' does not exist");
        }
        return channel;
    }


    @GraphQLMutation
    public boolean flushGames()
    {
        channelRepository.flush();

        return true;
    }


    @GraphQLMutation
    public boolean sendChatMessage(String secret, String message)
    {
        Channel channel = getChannel(secret);

        PreparedMessages preparedMessages = null;
        LogEntry logEntry = null;
        synchronized (channel)
        {
            logEntry = LogEntry.text(
                AppAuthentication.current().getLogin(),
                message
            );

            channel.getLogEntries().add(
                logEntry
            );

            Channel copy = channel.getMinimizedCopy();

            copy.getLogEntries().add(logEntry);

            preparedMessages = copy.prepareUpdate(copy.getUsers());

            channelRepository.updateChannel(channel);
        }

        preparedMessages.sendAll(skatWebSocketHandler);

        return true;
    }
}
