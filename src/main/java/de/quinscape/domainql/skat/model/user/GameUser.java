package de.quinscape.domainql.skat.model.user;

import de.quinscape.domainql.skat.runtime.service.AppAuthentication;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GameUser
{
    private final static Set<String> TEST_NAMES = ConcurrentHashMap.newKeySet();

    private final String name;

    private final GameUserType type;

    private final String connectionId;

    private final String id;

    private final boolean active;


    public static GameUser fromAuth(AppAuthentication auth, String connectionId)
    {
        final GameUserType type = GameUserType.getType(auth.getRoles());
        return  new GameUser(
            auth.getId(),
            type == GameUserType.TEST_USER ?
                createUnique(auth.getLogin()) :
                auth.getLogin(),
            type,
            connectionId,
            true
        );
    }

    public GameUser(String id, String name, GameUserType type, String connectionId, boolean active)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("name can't be null");
        }


        if (connectionId == null)
        {
            throw new IllegalArgumentException("connectionId can't be null");
        }

        if (type == null)
        {
            throw new IllegalArgumentException("type can't be null");
        }


        this.type = type;

        this.name = name;

        this.id = id;
        this.connectionId = connectionId;
        this.active = active;
    }

    public GameUser deactivate()
    {
        return new GameUser(
            id,
            name,
            type,
            connectionId,
            false
        );
    }

    private static String createUnique(String login)
    {
        int count = 2;

        String attempt = login;
        while (TEST_NAMES.contains(attempt))
        {
            attempt = login + (count++);
        }

        TEST_NAMES.add(attempt);

        return attempt;
    }


    public String getName()
    {
        return name;
    }

    public GameUserType getType()
    {
        return type;
    }


    public String getConnectionId()
    {
        return connectionId;
    }


    public String getId()
    {
        return id;
    }


    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        GameUser that = (GameUser) o;

        if (type != that.type)
        {
            return false;
        }

        if (type == GameUserType.TEST_USER)
        {
            return connectionId.equals(that.connectionId);
        }
        else
        {
            return name.equals(that.name);
        }
    }


    public boolean isActive()
    {
        return active;
    }


    @Override
    public int hashCode()
    {
        int result = type.hashCode();
        if (type == GameUserType.TEST_USER)
        {
            result = 31 * result + connectionId.hashCode();
        }
        else
        {
            result = 31 * result + name.hashCode();
        }
        return result;
    }


    @Override
    public String toString()
    {
        return super.toString() + ": "
            + "name = '" + name + '\''
            + ", connectionId = '" + connectionId + '\''
            + ", active = " + active
            ;
    }
}
