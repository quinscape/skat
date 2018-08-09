package de.quinscape.domainql.skat.runtime.config;

import de.quinscape.domainql.skat.domain.tables.pojos.AppUser;
import de.quinscape.domainql.skat.domain.Tables;
import org.jooq.DSLContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

/**
 * Fetches UserDetails based on our app_user table
 */
public class AppAuthenticationService
    implements UserDetailsService
{
    private final DSLContext dslContext;


    public AppAuthenticationService(DSLContext dslContext)
    {
        this.dslContext = dslContext;
    }


    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException
    {
        final List<AppUser> appUsers =
            dslContext.select()
                .from(Tables.APP_USER)
                .where(Tables.APP_USER.LOGIN.eq(userName))
                .fetchInto(AppUser.class);

        if (appUsers.size() != 1)
        {
            throw new UsernameNotFoundException("Could not find login with name '" + userName + "'");
        }
        return new AppUserDetails(appUsers.get(0));
    }
}
