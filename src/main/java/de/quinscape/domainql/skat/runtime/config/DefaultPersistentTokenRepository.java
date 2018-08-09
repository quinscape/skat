package de.quinscape.domainql.skat.runtime.config;

import de.quinscape.domainql.skat.domain.tables.pojos.AppLogin;
import de.quinscape.domainql.skat.domain.Tables;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Persistent Token repository based on the app_user table
 */
public class DefaultPersistentTokenRepository
    implements PersistentTokenRepository
{
    private final static Logger log = LoggerFactory.getLogger(DefaultPersistentTokenRepository.class);

    private final DSLContext dslContext;


    public DefaultPersistentTokenRepository(DSLContext dslContext)
    {
        this.dslContext = dslContext;
    }


    public void createNewToken(PersistentRememberMeToken token)
    {

        try
        {
            AppLogin login = new AppLogin();
            login.setUsername(token.getUsername());
            login.setToken(token.getTokenValue());
            login.setSeries(token.getSeries());
            login.setLastUsed(new Timestamp(token.getDate().getTime()));

            dslContext.executeInsert(dslContext.newRecord(Tables.APP_LOGIN, login));
        }
        catch (Exception e)
        {
            log.error("Error creating new token: " + token, e);
        }
    }


    public void updateToken(String series, String tokenValue, Date lastUsed)
    {
        try
        {
            final AppLogin login = findLoginForSeries(series);

            if (login == null)
            {
                throw new IllegalStateException("No token found for series " + series);
            }

            login.setToken(tokenValue);
            login.setLastUsed(new Timestamp(lastUsed.getTime()));

            dslContext.executeUpdate(dslContext.newRecord(Tables.APP_LOGIN, login));
        }
        catch (Exception e)
        {
            log.error("Error updating token: " + series + ", value = " + tokenValue + ", lastUsed = " + lastUsed, e);
        }
    }


    private AppLogin findLoginForSeries(String series)
    {
        final List<AppLogin> logins = dslContext.select()
            .from(Tables.APP_LOGIN)
            .where(
                Tables.APP_LOGIN.SERIES.eq(series)
            )
            .fetchInto(AppLogin.class);

        if (logins.size() != 1)
        {
            return null;
        }

        return logins.get(0);
    }


    /**
     * Loads the token data for the supplied series identifier.
     * <p>
     * If an error occurs, it will be reported and null will be returned (since the result
     * should just be a failed persistent login).
     *
     * @param seriesId      unique series identifier
     * @return the token matching the series, or null if no match found or an exception
     * occurred.
     */
    public PersistentRememberMeToken getTokenForSeries(String seriesId)
    {

        try
        {
            final AppLogin login = findLoginForSeries(seriesId);
            if (login == null)
            {
                return null;
            }

            return new PersistentRememberMeToken(
                login.getUsername(),
                login.getSeries(),
                login.getToken(),
                login.getLastUsed()
            );
        }
        catch (Exception e)
        {
            log.error("Error getting token for series" + seriesId, e);
            return null;
        }
    }


    public void removeUserTokens(String username)
    {
        dslContext.deleteFrom(Tables.APP_LOGIN).where(Tables.APP_LOGIN.USERNAME.eq(username));
    }
}
