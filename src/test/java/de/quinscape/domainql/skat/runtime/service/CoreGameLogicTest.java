package de.quinscape.domainql.skat.runtime.service;

import de.quinscape.domainql.skat.TestCards;
import de.quinscape.domainql.skat.model.channel.Channel;
import de.quinscape.domainql.skat.model.core.GameOptions;
import de.quinscape.domainql.skat.model.core.GameRound;
import de.quinscape.domainql.skat.model.core.Position;
import de.quinscape.domainql.skat.model.user.GameUser;
import de.quinscape.domainql.skat.runtime.game.NeutralShuffle;
import de.quinscape.domainql.skat.util.Base32;
import de.quinscape.domainql.skat.ws.SkatClientConnection;
import de.quinscape.domainql.skat.ws.SkatWebSocketHandler;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class CoreGameLogicTest
{
    private final static Logger log = LoggerFactory.getLogger(CoreGameLogicTest.class);

    private static final String CHANNEL_ID = "test-test-test";

    private ChannelRepository channelRepository = new InMemoryChannelRepository();

    private Random random = new Random(0);

    private SkatWebSocketHandler webSocketHandler= new SkatWebSocketHandler(Collections.emptyList());

    private final CoreGameLogic logic = new CoreGameLogic(
        channelRepository,
        random,
        webSocketHandler,
        new TestShufflingService()
    );

    private final AtomicReference<ClientState> refA = new AtomicReference<>();
    private final AtomicReference<ClientState> refB = new AtomicReference<>();
    private final AtomicReference<ClientState> refC = new AtomicReference<>();


    private final SkatClientConnection connectionA = registerConnection("test-a", refA::set);
    private final SkatClientConnection connectionB = registerConnection("test-b", refB::set);
    private final SkatClientConnection connectionC = registerConnection("test-c", refC::set);
    private final SkatClientConnection connectionD = registerConnection("test-c", refC::set);

    private SkatClientConnection registerConnection(
        String login, Consumer<ClientState> consumer
    )
    {
        final String connectionId = Base32.uuid();


        final AppAuthentication auth = new AppAuthentication(
            login,
            Collections.singleton("ROLE_USER"),
            "9f74ae0f-bb0b-4780-b1dd-ead77f69fc4c"
        );
        final TestSkatClientConnection connection = new TestSkatClientConnection(connectionId, auth, consumer);
        webSocketHandler.register(connection);
        return connection;
    }

    private Channel channel;
    {
        channel = new Channel(CHANNEL_ID);

        channel.setUsers(Arrays.asList(
            GameUser.fromAuth(connectionA.getAuth(), connectionA.getConnectionId(), null),
            GameUser.fromAuth(connectionB.getAuth(), connectionB.getConnectionId(), null),
            GameUser.fromAuth(connectionC.getAuth(), connectionC.getConnectionId(), null)
        ));

        newGameRound(0);
    }


    private void newGameRound(int currentDealer)
    {
        final GameRound current = GameRound.shuffleDeck(NeutralShuffle.INSTANCE, random, currentDealer, TestCards.SORTED_DECK,
            new GameOptions());
        current.setSeating(channel.getUsers());
        channel.setCurrent(current);


        channelRepository.updateChannel(channel);
    }


    @Test(expected = GameLogicException.class)
    public void testWrongDealer()
    {
        logic.deal(connectionB, CHANNEL_ID);
    }

    @Test(expected = GameLogicException.class)
    public void testWrongDealer2()
    {
        logic.deal(connectionD, CHANNEL_ID);
    }


    @Test
    public void testDealing()
    {
        logic.deal(connectionA, CHANNEL_ID);

        assertThat(refB.get().getChannel().getCurrent().getCurrentDealer(), is(0));

        assertThat(refA.get().getHand().getCards(), is(Arrays.asList(32, 8, 31, 30, 23, 22, 21, 20, 9, 7)));
        assertThat(refB.get().getHand().getCards(), is(Arrays.asList(24, 26, 25, 15, 14, 13, 12, 3, 2, 1)));
        assertThat(refC.get().getHand().getCards(), is(Arrays.asList(16, 29, 28, 27, 19, 18, 17, 6, 5, 4)));

        assertThat(refA.get().getHand().getCurrentPosition(), Matchers.is(Position.DEAL));
        assertThat(refB.get().getHand().getCurrentPosition(), is(Position.RESPOND));
        assertThat(refC.get().getHand().getCurrentPosition(), is(Position.BID));


    }

    @Test
    public void testNextDealing()
    {
        newGameRound(1);

        logic.deal(connectionB, CHANNEL_ID);

        assertThat(refB.get().getChannel().getCurrent().getCurrentDealer(), is(1));

        assertThat(refB.get().getHand().getCards(), is(Arrays.asList(32, 8, 31, 30, 23, 22, 21, 20, 9, 7)));
        assertThat(refC.get().getHand().getCards(), is(Arrays.asList(24, 26, 25, 15, 14, 13, 12, 3, 2, 1)));
        assertThat(refA.get().getHand().getCards(), is(Arrays.asList(16, 29, 28, 27, 19, 18, 17, 6, 5, 4)));

        assertThat(refA.get().getHand().getCurrentPosition(), is(Position.BID));
        assertThat(refB.get().getHand().getCurrentPosition(), is(Position.DEAL));
        assertThat(refC.get().getHand().getCurrentPosition(), is(Position.RESPOND));


    }
}
