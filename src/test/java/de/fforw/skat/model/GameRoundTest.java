package de.fforw.skat.model;

import de.quinscape.spring.jsview.util.JSONUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;

public class GameRoundTest
{

    private final static Logger log = LoggerFactory.getLogger(GameRoundTest.class);


    @Test
    public void name()
    {
        final GameRound game = GameRound.shuffleDeck(new SecureRandom());



        log.info(JSONUtil.DEFAULT_GENERATOR.forValue(game));
    }
}
