package de.quinscape.domainql.skat.runtime.config;

import de.quinscape.domainql.skat.model.user.GameUser;
import de.quinscape.domainql.skat.model.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;

/**
 * Makes sure the current user is one of the users registered in the the skat game
 */
public class SkatGamePermissionEvaluator
    implements PermissionEvaluator
{
    private final static Logger log = LoggerFactory.getLogger(SkatGamePermissionEvaluator.class);


    @Override
    public boolean hasPermission(
        Authentication authentication, Object targetDomainObject, Object permission
    )
    {

        log.info("hasPermission({}, {}, {}, {}", authentication, targetDomainObject, permission );

        final AppUserDetails appUserDetails = (AppUserDetails) authentication.getPrincipal();

        // not app user details?
        if (appUserDetails == null)
        {
            // -> no access
            return false;
        }

        if (targetDomainObject instanceof Channel)
        {
            final String userId = appUserDetails.getId();
            for (GameUser user : ((Channel) targetDomainObject).getUsers())
            {
                if (user.getName().equals(userId))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasPermission(
        Authentication authentication, Serializable targetId, String targetType, Object permission
    )
    {
        log.info("hasPermission2({}, {}, {}, {}", authentication, targetId, targetType, permission );

        // reject identifier only
        return false;
    }
}
