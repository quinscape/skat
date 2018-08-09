/*
 * This file is generated by jOOQ.
*/
package de.quinscape.domainql.skat.domain.tables.records;


import de.quinscape.domainql.skat.domain.tables.UserConfig;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;


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
@Entity
@Table(name = "user_config", schema = "public", indexes = {
    @Index(name = "fki_config_user_user", columnList = "user_id ASC"),
    @Index(name = "pk_user_config", unique = true, columnList = "id ASC"),
    @Index(name = "uc_user_config_user_id", unique = true, columnList = "user_id ASC")
})
public class UserConfigRecord extends UpdatableRecordImpl<UserConfigRecord> implements Record3<String, String, Boolean> {

    private static final long serialVersionUID = 922653924;

    /**
     * Setter for <code>public.user_config.id</code>.
     */
    public void setId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.user_config.id</code>.
     */
    @Id
    @Column(name = "id", unique = true, nullable = false, length = 36)
    @NotNull
    @Size(max = 36)
    public String getId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>public.user_config.user_id</code>.
     */
    public void setUserId(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.user_config.user_id</code>.
     */
    @Column(name = "user_id", unique = true, nullable = false, length = 36)
    @NotNull
    @Size(max = 36)
    public String getUserId() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.user_config.lock_bidding</code>.
     */
    public void setLockBidding(Boolean value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.user_config.lock_bidding</code>.
     */
    @Column(name = "lock_bidding")
    public Boolean getLockBidding() {
        return (Boolean) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<String, String, Boolean> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<String, String, Boolean> valuesRow() {
        return (Row3) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return UserConfig.USER_CONFIG.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return UserConfig.USER_CONFIG.USER_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field3() {
        return UserConfig.USER_CONFIG.LOCK_BIDDING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getUserId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean component3() {
        return getLockBidding();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getUserId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean value3() {
        return getLockBidding();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserConfigRecord value1(String value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserConfigRecord value2(String value) {
        setUserId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserConfigRecord value3(Boolean value) {
        setLockBidding(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserConfigRecord values(String value1, String value2, Boolean value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached UserConfigRecord
     */
    public UserConfigRecord() {
        super(UserConfig.USER_CONFIG);
    }

    /**
     * Create a detached, initialised UserConfigRecord
     */
    public UserConfigRecord(String id, String userId, Boolean lockBidding) {
        super(UserConfig.USER_CONFIG);

        set(0, id);
        set(1, userId);
        set(2, lockBidding);
    }
}
