package org.jasig.portlet.notice.rest;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Josh Helmer, jhelmer.unicon.net
 * @since 3.0
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class RecipientDTO implements Serializable {
    private static final long serialVersionUid = 1l;

    private long id;
    private String username;


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }
}
