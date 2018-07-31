/*
 * This file is generated by jOOQ.
*/
package de.fforw.skat.domain.tables.pojos;


import java.io.Serializable;
import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


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
@Table(name = "app_login", schema = "public", indexes = {
    @Index(name = "pk_app_login", unique = true, columnList = "series ASC")
})
public class AppLogin implements Serializable {

    private static final long serialVersionUID = 1708276533;

    private String    username;
    private String    series;
    private String    token;
    private Timestamp lastUsed;

    public AppLogin() {}

    public AppLogin(AppLogin value) {
        this.username = value.username;
        this.series = value.series;
        this.token = value.token;
        this.lastUsed = value.lastUsed;
    }

    public AppLogin(
        String    username,
        String    series,
        String    token,
        Timestamp lastUsed
    ) {
        this.username = username;
        this.series = series;
        this.token = token;
        this.lastUsed = lastUsed;
    }

    @Column(name = "username", nullable = false, length = 64)
    @NotNull
    @Size(max = 64)
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Id
    @Column(name = "series", unique = true, nullable = false, length = 64)
    @NotNull
    @Size(max = 64)
    public String getSeries() {
        return this.series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    @Column(name = "token", nullable = false, length = 64)
    @NotNull
    @Size(max = 64)
    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Column(name = "last_used", nullable = false)
    @NotNull
    public Timestamp getLastUsed() {
        return this.lastUsed;
    }

    public void setLastUsed(Timestamp lastUsed) {
        this.lastUsed = lastUsed;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("AppLogin (");

        sb.append(username);
        sb.append(", ").append(series);
        sb.append(", ").append(token);
        sb.append(", ").append(lastUsed);

        sb.append(")");
        return sb.toString();
    }
}