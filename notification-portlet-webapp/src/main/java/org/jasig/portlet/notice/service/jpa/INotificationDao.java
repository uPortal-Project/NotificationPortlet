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

import java.util.List;
import java.util.Set;

import org.jasig.portlet.notice.NotificationState;

/**
 * This DAO interface defines methods for the CrUD operations available for
 * JPA-flavor notification objects.  (E.g. {@link JpaEntry}.)  The JPA data
 * source (package o.j.p.n.service.jpa) follows a DAO+Service pattern:  the DAO
 * implements raw reads and updates against the back-end data store (RDBMS via
 * JPA/Hibernate), where the Service (bean) is responsible for implementing the
 * rules and logic that accompany reads and updates (e.g. validation,
 * sequencing, and access restrictions).  The original/primary service bean is
 * {@link JpaNotificationService}.  Access to JPA-flavor notification objects
 * from outside the o.j.p.n.service.jpa package occurs ONLY through this Service
 * bean (or similar).  For that reason, this interface is package-private.
 * 
 * @since 3.0
 * @author drewwills
 */
/* package-private */ interface INotificationDao {

    /**
     * Obtains an entry not including addressee info.
     */
    JpaEntry getEntry(long entryId);

    /**
     * Obtains an entry that includes addressee info.
     */
    JpaEntry getFullEntry(long entryId);

    JpaEntry createOrUpdateEntry(JpaEntry entry);

    List<JpaEntry> list(Integer page, Integer pageSize);

    List<JpaEntry> getNotificationsBySourceAndCustomAttribute(String source, String attributeName, String attributeValue);

    void removeEntry(JpaEntry entry);

    Set<JpaEntry> getEntriesByRecipient(String username);

    Set<JpaEntry> getEntriesByRecipientByStatus(String username, 
            Set<NotificationState> include, Set<NotificationState> exclude);

    JpaAddressee createOrUpdateAddressee(JpaAddressee addressee);
    JpaAddressee getAddressee(long addresseeId);

    List<JpaEvent> getEvents(long entryId);

    /**
     * Provides a complete transaction log for a notification and single
     * recipient <strong>in chronological order</strong>.
     */
    List<JpaEvent> getEvents(long entryId, String username);

    JpaEvent createOrUpdateEvent(JpaEvent event);

    JpaEvent getEvent(long eventId);
}
