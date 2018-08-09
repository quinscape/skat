package de.quinscape.domainql.skat.model.core;

/**
 * Additional announcements for the declarer
 */
public enum Announcement
{
    /**
     * Declarer does not pick up or see the skat.
     */
    HAND("hand"),
    /**
     * Opponents will score less than 30 points
     */
    SCHNEIDER_ANNOUNCED("hand schneider"),
    /**
     * Opponents will not get a single trick.
     */
    NO_TRICKS_ANNOUNCED("hand no-tricks"),

    /**
     * The cards will be open
     */
    OUVERT("hand ouvert");


    private final String description;


    Announcement(String description)
    {
        this.description = description;
    }


    public static Announcement valueOf(int announce)
    {
        if (announce < 0)
        {
            return null;
        }

        return Announcement.values()[announce];
    }


    public static String description(int announcement)
    {
        final Announcement value = Announcement.valueOf(announcement);
        return value == null ? "" : value.getDescription();
    }


    public String getDescription()
    {
        return description;
    }
}
