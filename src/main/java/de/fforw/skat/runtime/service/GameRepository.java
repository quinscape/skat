package de.fforw.skat.runtime.service;

import de.fforw.skat.domain.model.SkatGame;

public interface GameRepository
{
    SkatGame getGameById(String id);
    void updateGame(SkatGame gameRound);
}
