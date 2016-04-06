package org.jasig.portlet.notice.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.jasig.portlet.notice.NotificationState;

import java.io.Serializable;
import java.sql.Timestamp;


/**
 * @author Josh Helmer, jhelmer.unicon.net
 * @since 3.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EventDTO implements Serializable {
    private static final long serialVersionUid = 1l;

    private long id;
    private Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    private String username;
    private NotificationState state;


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public Timestamp getTimestamp() {
        return timestamp;
    }


    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public NotificationState getState() {
        return state;
    }


    public void setState(NotificationState state) {
        this.state = state;
    }
}
