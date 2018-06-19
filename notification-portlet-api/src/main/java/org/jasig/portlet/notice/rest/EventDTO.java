/*
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portlet.notice.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    @Override
    public String toString() {
        return "EventDTO{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", username='" + username + '\'' +
                ", state=" + state +
                '}';
    }

}
