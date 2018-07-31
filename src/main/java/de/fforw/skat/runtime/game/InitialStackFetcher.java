package de.fforw.skat.runtime.game;

import de.fforw.skat.domain.model.GamePhase;
import de.fforw.skat.domain.model.GameRound;
import de.fforw.skat.runtime.config.AppAuthentication;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.util.List;

public class InitialStackFetcher
    implements DataFetcher<List<Integer>>
{

    public InitialStackFetcher(String name, String data)
    {
        
    }


    @Override
    public List<Integer> get(DataFetchingEnvironment environment)
    {
        GameRound gameRound = environment.getSource();
        if (gameRound.getPhase() == GamePhase.FINISHED || AppAuthentication.current().getRoles().contains("ROLE_ADMIN"))
        {
            return gameRound.getInitialStack();
        }
        else
        {
            throw new IllegalStateException("Cannot query initialStack of unfinished game");
        }
    }
}
