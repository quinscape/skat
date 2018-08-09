package de.quinscape.domainql.skat.runtime.game;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public interface ShufflingStrategy
{
    List<Integer> shuffle(Random random, List<Integer> cards);
}
