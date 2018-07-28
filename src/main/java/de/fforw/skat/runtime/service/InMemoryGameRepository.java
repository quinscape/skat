package de.fforw.skat.runtime.service;

import de.fforw.skat.domain.model.SkatGame;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryGameRepository
    implements GameRepository
{

    private final ConcurrentMap<String, SkatGame> games = new ConcurrentHashMap<>();
    
    @Override
    public SkatGame getGameById(String id)
    {
        return games.get(id);
    }

    @Override
    public void updateGame(SkatGame skatGame)
    {
        games.put(skatGame.getId(), skatGame);
    }
}
