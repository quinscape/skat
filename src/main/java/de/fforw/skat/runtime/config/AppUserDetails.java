package de.fforw.skat.runtime.config;

import de.fforw.skat.domain.tables.pojos.AppUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

/**
 * Immutable user details implementation for app.
 */
public class AppUserDetails
    implements UserDetails
{

    private final Set<GrantedAuthority> authorities;

    private final String id;

    private final String password;

    private final String login;

    private final Timestamp lastLogin;

    private final Set<String> roles;

    public AppUserDetails(AppUser appUser)
    {
        this.id = appUser.getId();
        this.roles = splitRoles(appUser.getRoles());

        this.login = appUser.getLogin();
        this.authorities = this.roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
        this.password = appUser.getPassword();
        this.lastLogin = appUser.getLastLogin();
    }

    public Set<String> getRoles()
    {
        return roles;
    }


    public Timestamp getLastLogin()
    {
        return lastLogin;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return authorities;
    }


    @Override
    public String getPassword()
    {
        return password;
    }


    @Override
    public String getUsername()
    {
        return login;
    }


    @Override
    public boolean isAccountNonExpired()
    {
        return true;
    }


    @Override
    public boolean isAccountNonLocked()
    {
        return true;
    }


    @Override
    public boolean isCredentialsNonExpired()
    {
        return true;
    }


    @Override
    public boolean isEnabled()
    {
        return true;
    }

    public static Set<String> splitRoles(String s)
    {
        StringTokenizer tokenizer = new StringTokenizer(s, ",");
        Set<String> set = new HashSet<>();
        while (tokenizer.hasMoreElements())
        {
            set.add( tokenizer.nextToken().trim());
        }
        return set;
    }


    public String getId()
    {
        return id;
    }
}
