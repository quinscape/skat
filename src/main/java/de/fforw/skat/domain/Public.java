/*
 * This file is generated by jOOQ.
*/
package de.fforw.skat.domain;


import de.fforw.skat.domain.tables.AppLogin;
import de.fforw.skat.domain.tables.AppUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.6"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Public extends SchemaImpl {

    private static final long serialVersionUID = 1233400338;

    /**
     * The reference instance of <code>public</code>
     */
    public static final Public PUBLIC = new Public();

    /**
     * The table <code>public.app_login</code>.
     */
    public final AppLogin APP_LOGIN = de.fforw.skat.domain.tables.AppLogin.APP_LOGIN;

    /**
     * The table <code>public.app_user</code>.
     */
    public final AppUser APP_USER = de.fforw.skat.domain.tables.AppUser.APP_USER;

    /**
     * No further instances allowed
     */
    private Public() {
        super("public", null);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        List result = new ArrayList();
        result.addAll(getTables0());
        return result;
    }

    private final List<Table<?>> getTables0() {
        return Arrays.<Table<?>>asList(
            AppLogin.APP_LOGIN,
            AppUser.APP_USER);
    }
}
