/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.portlet.notice.service.jpa;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.jasig.portlet.notice.NotificationState;

/**
 * Represents an entry in the transaction log for a notification within the 
 * {@link JpaNotificationService}.
 *
 * @since 3.0
 * @author drewwills
 */
@Entity
@Table(name=JpaNotificationService.TABLENAME_PREFIX + "EVENT")
/* package-private */ class JpaEvent {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="ID", nullable = false)
    private long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ENTRY_ID")
    private JpaEntry entry;

    @Column(name="USERNAME", nullable=false)
    private String username;

    @Column(name="TIMESTAMP", nullable=true)
    private Timestamp timestamp;

    @Column(name="STATE", nullable=false)
    private NotificationState state;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public JpaEntry getEntry() {
        return entry;
    }

    public void setEntry(JpaEntry entry) {
        this.entry = entry;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public NotificationState getState() {
        return state;
    }

    public void setState(NotificationState state) {
        this.state = state;
    }

}
