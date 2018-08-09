package de.quinscape.domainql.skat.model.user;

import java.util.Set;

public enum GameUserType
{
    USER,
    TEST_USER,
    ADMIN;


    public static GameUserType getType(Set<String> roles)
    {
        if (roles.contains(Roles.ADMIN))
        {
            return ADMIN;
        }
        else if (roles.contains(Roles.TEST))
        {
            return TEST_USER;
        }
        return USER;
    }
}
