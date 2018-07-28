package de.fforw.skat.runtime.config;

import de.quinscape.spring.jsview.util.JSONUtil;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.svenson.info.JSONClassInfo;
import org.svenson.info.JSONPropertyInfo;

import java.io.Serializable;

public class OwnerPermissionEvaluator
    implements PermissionEvaluator
{
    private static final String DEFAULT_OWNER_PROPERTY_NAME = "ownerId";

    private final String ownerPropertyName;

    private final MissingOwner missingOwner;


    /**
     * Creates a new OwnerPermissionEvaluator with the default property name (@link
     * {@link #DEFAULT_OWNER_PROPERTY_NAME }
     */
    public OwnerPermissionEvaluator()
    {
        this(DEFAULT_OWNER_PROPERTY_NAME, MissingOwner.ERROR);
    }


    /**
     * Creates a new OwnerPermissionEvaluator with given property name
     *
     * @param ownerPropertyName     ownerId property name
     * @param missingOwner          missingOwner enum
     */
    public OwnerPermissionEvaluator(
        String ownerPropertyName,
        MissingOwner missingOwner
    )
    {
        this.ownerPropertyName = ownerPropertyName;
        this.missingOwner = missingOwner;
    }


    @Override
    public boolean hasPermission(
        Authentication authentication, Object targetDomainObject, Object permission
    )
    {
        final AppUserDetails appUserDetails = (AppUserDetails) authentication.getPrincipal();

        // not app user details?
        if (appUserDetails == null)
        {
            // -> no access
            return false;
        }

        final JSONClassInfo classInfo = JSONUtil.getClassInfo(targetDomainObject.getClass());
        final JSONPropertyInfo propertyInfo = classInfo.getPropertyInfo("ownerId");

        // no "ownerId" property?
        if (propertyInfo == null)
        {
            // -> object not under access control
            return true;
        }

        final Object ownerId = JSONUtil.DEFAULT_UTIL.getProperty(targetDomainObject, "ownerId");
        if (ownerId == null)
        {
            if (missingOwner == MissingOwner.ERROR)
            {
                throw new IllegalStateException("Owner id not set on " + targetDomainObject);
            }
            else if (missingOwner == MissingOwner.ALLOW_ACCESS)
            {
                return true;
            }
            else
            {
                throw new IllegalStateException("Unhandled enum value" + missingOwner);
            }
        }

        return ownerId.equals(appUserDetails.getId());
    }


    @Override
    public boolean hasPermission(
        Authentication authentication, Serializable targetId, String targetType, Object permission
    )
    {
        // reject identifier only
        return false;
    }
}
