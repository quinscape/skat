package de.fforw.skat.runtime.service;

import de.fforw.skat.model.Bidding;
import de.fforw.skat.model.GamePhase;
import de.fforw.skat.model.GameRound;
import de.fforw.skat.model.GameUser;
import de.fforw.skat.model.Position;
import de.fforw.skat.model.channel.Channel;
import de.fforw.skat.runtime.HandFetcher;
import de.fforw.skat.runtime.message.PreparedMessages;
import de.fforw.skat.ws.SkatClientConnection;
import de.fforw.skat.ws.SkatWebSocketHandler;
import de.quinscape.domainql.annotation.GraphQLLogic;
import de.quinscape.domainql.annotation.GraphQLMutation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;
import java.util.Random;

/**
 * Handles the core logic of the game. 
 */
@GraphQLLogic
public class CoreGameLogic
{
    private final static Logger log = LoggerFactory.getLogger(CoreGameLogic.class);

    private final GameRepository gameRepository;

    private final Random random;
    private final SkatWebSocketHandler skatWebSocketHandler;

    @Autowired
    public CoreGameLogic(
        GameRepository gameRepository,
        Random random,
        SkatWebSocketHandler skatWebSocketHandler
    )
    {
        this.gameRepository = gameRepository;
        this.random = random;
        this.skatWebSocketHandler = skatWebSocketHandler;
    }


    @GraphQLMutation
    public boolean deal(SkatClientConnection conn,String secret)
    {
        final String connectionId = conn.getConnectionId();
        final Channel channel = gameRepository.getChannelById(secret);

        final GameRound current = channel.getCurrent();
        final int seatIndex = HandFetcher.findSeatByConnection(current.getSeating(), connectionId);

        final int currentPosition;

        if (current.getPhase() != GamePhase.OPEN || seatIndex < 0 || (currentPosition = HandFetcher.getCurrentPosition(
            seatIndex, current.getCurrentDealer(), current.getNumberOfSeats())) != 0)
        {
            return false;
        }

        PreparedMessages preparedMessages = null;
        synchronized (channel)
        {
            current.setPhase(GamePhase.BIDDING);
            current.setLastUpdated(Instant.now().toString());
            preparedMessages = channel.prepareUpdate(null, "Dealer dealt all cards.");
            gameRepository.updateChannel(channel);
        }

        preparedMessages.sendAll(skatWebSocketHandler);
        return true;
    }


    @GraphQLMutation
    public boolean accept(SkatClientConnection conn, String secret, int value)
    {

        final String connectionId = conn.getConnectionId();
        final Channel channel = gameRepository.getChannelById(secret);

        final GameRound current = channel.getCurrent();
        final int numberOfSeats = current.getNumberOfSeats();
        final int seatIndex = HandFetcher.findSeatByConnection(current.getSeating(), connectionId);
        if (current.getPhase() != GamePhase.BIDDING || seatIndex < 0)
        {
            return false;
        }

        final Position currentPosition = Position.values()[HandFetcher.getCurrentPosition(
            seatIndex,
            current.getCurrentDealer(),
            numberOfSeats
        )];

        final Bidding bidding = current.getBidding();

        PreparedMessages preparedMessages = null;
        synchronized (channel)
        {
            final Bidding newBidding = bidding.accepted(currentPosition, value, numberOfSeats);
            current.setBidding(newBidding);
            current.setLastUpdated(Instant.now().toString());
            preparedMessages = channel.prepareUpdate(null, current.getSeating().get(seatIndex).getName() + (currentPosition.ordinal() == newBidding.getBidder() ? " bids " : " accepts ")  + value);
            gameRepository.updateChannel(channel);
        }

        preparedMessages.sendAll(skatWebSocketHandler);
        return true;
    }

    @GraphQLMutation
    public boolean pass(SkatClientConnection conn,String secret)
    {
        log.debug("pass({},{})", conn, secret);
        final String connectionId = conn.getConnectionId();

        final Channel channel = gameRepository.getChannelById(secret);

        final GameRound current = channel.getCurrent();
        final int seatIndex = HandFetcher.findSeatByConnection(current.getSeating(), connectionId);

        int currentPosition = -1;

        final int numberOfSeats = current.getNumberOfSeats();
        if (current.getPhase() != GamePhase.BIDDING || seatIndex < 0 || (currentPosition = HandFetcher.getCurrentPosition(
            seatIndex, current.getCurrentDealer(), numberOfSeats)) == -1)
        {
            log.debug("Ignore pass: phase = {}, seatIndex = {}, currentPosition = {}", current.getPhase(), seatIndex, currentPosition);
            return false;
        }

        PreparedMessages preparedMessages = null;
        synchronized (channel)
        {
            final Bidding newBidding = current.getBidding().pass(currentPosition, channel.getCurrent().getOptions().isAllowContinue());
            current.setBidding(newBidding);
            current.setLastUpdated(Instant.now().toString());

            if (newBidding.isFinished())
            {
                current.setPhase(GamePhase.DECLARING);
            }
            preparedMessages = channel.prepareUpdate(null, current.getSeating().get(seatIndex).getName() + " passes.");
            gameRepository.updateChannel(channel);
        }

        preparedMessages.sendAll(skatWebSocketHandler);
        return true;
    }

    @GraphQLMutation
    public boolean sit(SkatClientConnection conn, String secret, int seatIndex)
    {

        final Channel channel = gameRepository.getChannelById(secret);

        final GameRound current = channel.getCurrent();
        final List<GameUser> seating = current.getSeating();
        final String connectionId = conn.getConnectionId();
        final int currentSeat = HandFetcher.findSeatByConnection(seating, connectionId);

        PreparedMessages preparedMessages = null;

        if (current.getPhase() != GamePhase.OPEN)
        {
            return false;
        }


        synchronized (channel)
        {
            if (currentSeat > 0)
            {
                seating.set(currentSeat, null);
            }

            seating.set(seatIndex, GameUser.fromAuth(conn.getAuth(), conn.getConnectionId()));
            preparedMessages = channel.prepareUpdate(null, seating.get(currentSeat).getName() + " sits on Seat #" + seatIndex + ".");
            gameRepository.updateChannel(channel);
        }

        preparedMessages.sendAll(skatWebSocketHandler);
        return true;
    }
}
