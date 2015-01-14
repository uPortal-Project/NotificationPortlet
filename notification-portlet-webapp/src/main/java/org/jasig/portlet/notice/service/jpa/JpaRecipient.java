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

import java.util.Collections;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Represents an individual who receives a notification.
 *
 * @author drewwills
 */
@Entity
@Table(name=JpaNotificationService.TABLENAME_PREFIX + "RECIPIENT")
/* package-private */ class JpaRecipient {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="ID", nullable = false)
    private long id;

    @Column(name="ADDRESSEE_ID", nullable = false)
    private long addresseeId;

    @Column(name="USERNAME", nullable=false)
    private String username;

    @OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinColumn(name="RECIPIENT_ID")
    private Set<JpaEvent> events = Collections.emptySet();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAddresseeId() {
        return addresseeId;
    }

    public void setAddresseeId(long addresseeId) {
        this.addresseeId = addresseeId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Provides a read-only copy of this notification's events.
     */
    public Set<JpaEvent> getEvents() {
        return Collections.unmodifiableSet(events);
    }

    /**
     * Replaces the current events with the contents of the provided collection.
     */
    public void setEvents(Set<JpaEvent> events) {
        this.events.clear();
        this.events.addAll(events);
    }

    /**
     * Adds the specified event to the current collection.
     */
    public void addEvent(JpaEvent event) {
        events.add(event);
    }

}
