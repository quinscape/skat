package de.quinscape.domainql.skat.runtime.game;

import de.quinscape.domainql.skat.model.core.GamePhase;
import de.quinscape.domainql.skat.model.core.GameRound;
import de.quinscape.domainql.skat.model.user.Roles;
import de.quinscape.domainql.skat.runtime.service.AppAuthentication;
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
        if (gameRound.getPhase() == GamePhase.FINISHED || AppAuthentication.current().getRoles().contains(Roles.ADMIN))
        {
            return gameRound.getInitialStackInternal();
        }
        else
        {
            throw new IllegalStateException("Cannot query initialStack of unfinished game");
        }
    }
}
