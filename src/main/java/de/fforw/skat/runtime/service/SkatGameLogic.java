package de.fforw.skat.runtime.service;

import de.fforw.skat.domain.model.SkatGame;
import de.fforw.skat.runtime.config.AppAuthentication;
import de.quinscape.domainql.annotation.GraphQLLogic;
import de.quinscape.domainql.annotation.GraphQLMutation;
import de.quinscape.domainql.annotation.GraphQLQuery;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@GraphQLLogic
public class SkatGameLogic
{
    private final static Logger log = LoggerFactory.getLogger(SkatGameLogic.class);


    private final DSLContext dslContext;
    private final GameRepository gameRepository;


    @Autowired
    public SkatGameLogic(DSLContext dslContext, GameRepository gameRepository)
    {
        this.dslContext = dslContext;
        this.gameRepository = gameRepository;
    }

    @GraphQLQuery
    public SkatGame skateGame(String id)
    {
        return gameRepository.getGameById(id);
    }

    @GraphQLMutation
    public SkatGame joinGame(String secret, String windowId)
    {
        SkatGame game = gameRepository.getGameById(secret);

        if (game == null)
        {
            game = new SkatGame(secret);
        }

        final AppAuthentication current = AppAuthentication.current();

        game.getUsers().add(
            getUserName(secret, windowId)
        );
        gameRepository.updateGame(game);

        return game;
    }


    private String getUserName(String secret, String windowId)
    {
        final AppAuthentication current = AppAuthentication.current();
        return current.getRoles().contains("ROLE_TEST") ?
            secret + ":" + windowId :
            secret;
    }


}
