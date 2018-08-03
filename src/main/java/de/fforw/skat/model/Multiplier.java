package de.fforw.skat.model;

/**
 * Multiplier for game values. the "game" modifier itself is implied.
 */
public enum Multiplier
{
    /**
     * HAND itself can be selected without HAND being a current multiplier.
     */
    HAND,
    SCHNEIDER,
    SCHNEIDER_ANNOUNCED(true),
    SCHWARZ,
    SCHWARZ_ANNOUNCED(true),
    OUVERT(true);

    private final boolean onlyWithHand;

    Multiplier()
    {
        this(false);
    }
    Multiplier(boolean onlyWithHand)
    {

        this.onlyWithHand = onlyWithHand;
    }


    /**
     * True if the game mode is only available if combined with the {@link #HAND} multiplier.
     *
     * @return
     */
    public boolean isOnlyWithHand()
    {
        return onlyWithHand;
    }
}
