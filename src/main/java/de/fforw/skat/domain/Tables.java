/*
 * This file is generated by jOOQ.
*/
package de.fforw.skat.domain;


import de.fforw.skat.domain.tables.AppLogin;
import de.fforw.skat.domain.tables.AppUser;

import javax.annotation.Generated;


/**
 * Convenience access to all tables in public
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.6"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Tables {

    /**
     * The table <code>public.app_login</code>.
     */
    public static final AppLogin APP_LOGIN = de.fforw.skat.domain.tables.AppLogin.APP_LOGIN;

    /**
     * The table <code>public.app_user</code>.
     */
    public static final AppUser APP_USER = de.fforw.skat.domain.tables.AppUser.APP_USER;
}