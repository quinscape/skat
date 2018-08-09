package de.quinscape.domainql.skat.model.channel;

import de.quinscape.domainql.skat.util.Cards;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class LogEntry
{

    private final static AtomicInteger ID_COUNTER = new AtomicInteger(0);

    /**
     * Pseudo-user name for system messages
     */
    public static final String SYSTEM = "SYSTEM";

    private EntryType type;
    private String user;
    private String timestamp;
    private String message;
    private boolean isAction;
    private int card;
    private int id;


    public LogEntry()
    {
        this(0, null, null, null, false, Cards.FACE_DOWN_CARD);
    }

    public LogEntry(int id, EntryType type, String user, String message, boolean isAction, int card)
    {
        this.id = id;
        this.type = type;
        this.user = user;
        this.timestamp = Instant.now().toString();
        this.message = message;
        this.isAction = isAction;
        this.card = card;
    }

    public static LogEntry text(String message)
    {
        return new LogEntry(
            ID_COUNTER.getAndIncrement(),
            EntryType.TEXT, SYSTEM,
            message,
            true,
            Cards.FACE_DOWN_CARD
        );
    }

    public static LogEntry text(String user, String message)
    {
        return new LogEntry(
            ID_COUNTER.getAndIncrement(),
            EntryType.TEXT,
            user,
            message,
            true,
            Cards.FACE_DOWN_CARD
        );
    }

    public static LogEntry action(String user, String message)
    {
        return new LogEntry(
            ID_COUNTER.getAndIncrement(),
            EntryType.ACTION,
            user,
            message,
            true,
            Cards.FACE_DOWN_CARD
        );
    }


    public static LogEntry card(String name, int card)
    {
        return new LogEntry(
            ID_COUNTER.getAndIncrement(),
            EntryType.CARD,
            name,
            null,
            true,
            card
        );
    }


    public static LogEntry win(String message)
    {
        return new LogEntry(
            ID_COUNTER.getAndIncrement(),
            EntryType.WIN,
            SYSTEM,
            message,
            true,
            Cards.FACE_DOWN_CARD
        );
    }


    public static LogEntry gameWin(String message)
    {
        return new LogEntry(
            ID_COUNTER.getAndIncrement(),
            EntryType.WIN_GAME,
            SYSTEM,
            message,
            true,
            Cards.FACE_DOWN_CARD
        );
    }


    public static LogEntry skat(int card)
    {
        return new LogEntry(
            ID_COUNTER.getAndIncrement(),
            EntryType.SKAT,
            SYSTEM,
            null,
            true,
            card
        );
    }


    public int getId()
    {
        return id;
    }


    public void setId(int id)
    {
        this.id = id;
    }


    public EntryType getType()
    {
        return type;
    }


    public void setType(EntryType type)
    {
        this.type = type;
    }


    public String getUser()
    {
        return user;
    }


    public void setUser(String user)
    {
        this.user = user;
    }


    public String getTimestamp()
    {
        return timestamp;
    }


    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }


    public String getMessage()
    {
        return message;
    }


    public void setMessage(String message)
    {
        this.message = message;
    }


    public boolean isAction()
    {
        return isAction;
    }


    public void setAction(boolean action)
    {
        isAction = action;
    }


    public int getCard()
    {
        return card;
    }

    public void setCard(int card)
    {
        this.card = card;
    }
}
