package de.fforw.skat.runtime.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Limited view on the app user details for client consumption.
 * 
 */
public class AppAuthentication
{
    private static final AppAuthentication ANONYMOUS;

    static {

        Set<String> roles = new LinkedHashSet<>();
        roles.add("ROLE_ANONYMOUS");
        ANONYMOUS = new AppAuthentication("anonymous", roles, null);
    }

    private final String login;
    private final Set<String> roles;

    private final String id;


    private AppAuthentication(String login, Set<String> roles, String id)
    {
        this.login = login;
        this.roles = roles;
        this.id = id;
    }

    /**
     * Accesses the spring security context to get the current AppUserDetails.
     * For anonymous users, {@link #ANONYMOUS} auth is returned.
     * @return
     */
    public static AppAuthentication current()
    {
        SecurityContext context = SecurityContextHolder.getContext();

        Authentication authentication = context.getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AppUserDetails)
        {
            final AppUserDetails details = (AppUserDetails) authentication.getPrincipal();
            return new AppAuthentication(details.getUsername(), details.getRoles(), details.getId());
        }
        if (authentication != null && authentication.getPrincipal() instanceof AppAuthentication)
        {
            return (AppAuthentication) authentication.getPrincipal();
        }
        else
        {
            return ANONYMOUS;
        }
    }


    public String getLogin()
    {
        return login;
    }


    public Set<String> getRoles()
    {
        return roles;
    }


    public String getId()
    {
        return id;
    }


    @Override
    public String toString()
    {
        return super.toString() + ": "
            + "login = '" + login + '\''
            + ", roles = " + roles
            + ", id = '" + id + '\''
            ;
    }
}
