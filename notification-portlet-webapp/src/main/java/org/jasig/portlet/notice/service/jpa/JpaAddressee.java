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
package org.jasig.portlet.notice.service.jpa;

import org.jasig.portlet.notice.rest.RecipientType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Represents a named addressee for a notification.  There are two types of
 * addressee:  individual and group.  The addressees list is the target(s) of
 * the notification as the author selected them, e.g. "send this notification to
 * graduating seniors."  The (aggregated) recipients list is the list of all
 * users who received the notification, e.g. "johndoe," "janedoe," and
 * "robsmith."  A single user <em>may</em> belong to two (or more) addressee
 * groups for a single notification, but should only see <em>one copy</em> of
 * the notification in such a case.
 *
 * @since 3.0
 * @author drewwills
 */
@Entity
@Table(name=JpaNotificationService.TABLENAME_PREFIX + "ADDRESSEE")
/* package-private */ class JpaAddressee {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="ID", nullable = false)
    private long id;

    @ManyToOne
    @JoinColumn(name="ENTRY_ID")
    private JpaEntry entry;

    @Column(name="NAME", nullable=false)
    private String name;

    @Column(name="TYPE", nullable=false)
    private RecipientType type;

    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    private Set<JpaRecipient> recipients = new HashSet<JpaRecipient>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public JpaEntry getEntryId() {
        return entry;
    }

    public void setEntry(JpaEntry entry) {
        this.entry = entry;
    }

    public RecipientType getType() {
        return type;
    }

    public void setType(RecipientType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Provides a read-only copy of this addressee's recipients.
     */
    public Set<JpaRecipient> getRecipients() {
        return Collections.unmodifiableSet(recipients);
    }

    /**
     * Replaces the current recipients with the contents of the specified set.
     */
    public void setRecipients(Set<JpaRecipient> recipients) {
        this.recipients.clear();
        this.recipients.addAll(recipients);
    }

    /**
     * Adds the specified recipient to the current collection.
     */
    public void addRecipient(JpaRecipient recipient) {
        recipients.add(recipient);
    }

    @Override
    public String toString() {
        return "JpaAddressee [id=" + id + ", name=" + name + ", type=" + type + ", recipients=" + recipients + "]";
    }

}
