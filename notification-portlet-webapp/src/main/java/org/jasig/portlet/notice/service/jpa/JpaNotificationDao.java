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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.Validate;
import org.jasig.portlet.notice.NotificationState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * This DAO class handles raw CRUD operations for the JPA-flavor notifications.
 *
 * @since 3.0
 * @author drewwills
 */
@Repository
/* package-private */ class JpaNotificationDao implements INotificationDao {

    @PersistenceContext
    private EntityManager entityManager;

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Search for a JpaEntry with the specified Id. If the entry exists in the
     * persistence context, it is returned;  otherwise, null is returned.
     */
    @Override
    @Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
    public JpaEntry getEntry(long entryId) {
        Validate.isTrue(entryId > 0, "Invalid entryId:  " + entryId);

        JpaEntry rslt = entityManager.find(JpaEntry.class, entryId);
        return rslt;
    }

    @Override
    @Transactional
    public JpaEntry createOrUpdateEntry(JpaEntry entry) {
        Validate.notNull(entry, "Argument 'entry' cannot be null");

        JpaEntry rslt = entityManager.merge(entry);
        return rslt;
    }

    @Override
    @Transactional
    public void removeEntry(JpaEntry entry) {
        Validate.notNull(entry, "Argument 'entry' cannot be null");

        JpaEntry y = entityManager.merge(entry);  // Insures that cascades will be handled properly
        entityManager.remove(y);
    }

    @Override
    @Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
    public Set<JpaEntry> getEntriesByRecipient(String username) {
        Validate.notEmpty(username, "Argument 'username' cannot be empty");

        final String jpql = "SELECT DISTINCT v.entry FROM JpaEvent v "
                                    + "WHERE v.username = :username";
        TypedQuery<JpaEntry> query = entityManager.createQuery(jpql, JpaEntry.class);

        log.debug("Query getEntriesByRecipient={}", query.toString());

        query.setParameter("username", username);
        List<JpaEntry> rslt = query.getResultList();
        return new HashSet<JpaEntry>(rslt);
    }

    @Override
    @Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
    public Set<JpaEntry> getEntriesByRecipientByStatus(String username,
            Set<NotificationState> include, Set<NotificationState> exclude) {
        Validate.notEmpty(username, "Argument 'username' cannot be empty");

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    @Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
    public List<JpaEvent> getEvents(long entryId, String username) {
        Validate.isTrue(entryId > 0, "Argument 'entryId' must be greater than zero (0)");
        Validate.notEmpty(username, "Argument 'username' cannot be empty");

        final String jpql = "SELECT v FROM JpaEvent v WHERE v.username = :username "
                + "AND v.entry = (SELECT e FROM JpaEntry e WHERE e.id = :entryId) "
                + "ORDER BY v.timestamp";
        TypedQuery<JpaEvent> query = entityManager.createQuery(jpql, JpaEvent.class);
        query.setParameter("username", username);
        query.setParameter("entryId", entryId);
        List<JpaEvent> rslt = query.getResultList();
        return rslt;
    }

	@Override
    @Transactional
    public JpaEvent createOrUpdateEvent(JpaEvent event) {
        Validate.notNull(event, "Argument 'event' cannot be null");

        JpaEvent rslt = entityManager.merge(event);
        return rslt;
    }
}
